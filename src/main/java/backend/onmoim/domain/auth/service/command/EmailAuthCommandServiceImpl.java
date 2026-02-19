package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.converter.EmailAuthConverter;
import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.repository.EmailAuthRepository;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Service
@Transactional
@SuppressWarnings("unchecked")
class EmailAuthCommandServiceImpl implements EmailAuthCommandService {

    private final StringRedisTemplate redisTemplate;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailAuthLogService emailAuthLogService;
    private final List<JavaMailSender> javaMailSenders;
    private final UserQueryRepository userQueryRepository;
    private final RestTemplate restTemplate; // 타임아웃이 적용된 RestTemplate 사용
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.cloudflare.turnstile.secret-key}")
    private String turnstileSecret;

    @Value("${spring.cloudflare.turnstile.verify-url}")
    private String turnstileVerifyUrl;

    @Value("${spring.cloudflare.turnstile.test-token}") //테스트용 토큰
    private String testToken;


     //생성자에서 RestTemplateBuilder를 주입받아 타임아웃을 설정
     public EmailAuthCommandServiceImpl(
             StringRedisTemplate redisTemplate,
             EmailAuthRepository emailAuthRepository,
             UserQueryRepository userQueryRepository,
             EmailAuthLogService emailAuthLogService,
             List<JavaMailSender> javaMailSenders,
             RestTemplateBuilder restTemplateBuilder) {
         this.redisTemplate = redisTemplate;
         this.emailAuthRepository = emailAuthRepository;
         this.userQueryRepository = userQueryRepository;
         this.emailAuthLogService = emailAuthLogService;
         this.javaMailSenders = new CopyOnWriteArrayList<>(javaMailSenders);


         this.restTemplate = restTemplateBuilder
                 .connectTimeout(Duration.ofSeconds(3))
                 .readTimeout(Duration.ofSeconds(3))
                 .build();
     }

    @Override
    public EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request, String ip) {

         // 봇 방지 검증 (Turnstile)
        verifyTurnstile(request.turnstileToken());

         // 동일 이메일 발송 쿨타임 체크-30초
        String cooldownKey = "auth:cooldown:" + request.email();
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "true", Duration.ofSeconds(30));
        if (Boolean.FALSE.equals(isSet)) {
            throw new EmailAuthException(EmailAuthErrorCode.RATE_LIMITED);
        }


        //회원유무 조회
        boolean isRegistered = userQueryRepository.existsByEmail(request.email());

        // 인증 정보 DB 저장
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        String authKey = "auth:email:" + request.email();

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(request.email())
                .code(code)
                .hashedIp(hashIp(ip))
                .isUsed(false)
                .build());

        boolean isSent = false;
        // 발송 계정 순회 및 발송 시도
        for (JavaMailSender sender : javaMailSenders) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) sender;
            String username = impl.getUsername();
            String usageKey = "auth:mail:usage:" + username;

            //  원자적인 increment를 먼저 수행하여 동시성 문제를 해결 - 선점형
            Long usageCount = redisTemplate.opsForValue().increment(usageKey);

            // 한도(500건) 체크 후 초과 시 카운트를 롤백하고 다음 계정으로 이동
            if (usageCount != null && usageCount > 500) {
                redisTemplate.opsForValue().decrement(usageKey);
                continue;
            }

            // 그날의 첫 발송인 경우 24시간 후 자동 초기화되도록 설정
            if (usageCount != null && usageCount == 1) {
                redisTemplate.expire(usageKey, Duration.ofDays(1));
            }

            try {
                // 메일 발송
                sendMail(sender, request.email(), code);

                // 성공 시 인증 코드 Redis 저장, TTL 5분
                redisTemplate.opsForValue().set(authKey, code, Duration.ofMinutes(5));
                isSent = true;
                break;
            } catch (Exception e) {
                // 발송 실패 시 선점했던 사용량 카운트를 다시 감소(롤백)시킴
                log.error("메일 발송 실패 - 계정: {}, 사유: {}. 카운트 롤백 및 후순위 이동.", username, e.getMessage());
                redisTemplate.opsForValue().decrement(usageKey);

                // 실패한 계정은 리스트 맨 뒤로 이동
                javaMailSenders.remove(sender);
                javaMailSenders.add(sender);
            }
        }

        // 모든 계정이 실패했거나 한도를 초과한 경우
        if (!isSent) {
            redisTemplate.delete(cooldownKey);
            emailAuthRepository.delete(emailAuth);
            throw new EmailAuthException(EmailAuthErrorCode.ALL_MAIL_ACCOUNTS_EXHAUSTED);
        }

        return EmailAuthConverter.toResultDTO(request.email(), 300L, isRegistered);
    }

    // 메일전송 로직
    private void sendMail(JavaMailSender mailSender, String to, String code) {
        JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(impl.getUsername());
        message.setTo(to);
        message.setSubject("[ONMOIM] 인증 코드 안내");
        message.setText("인증 코드: [" + code + "] 입니다. 5분 이내에 입력해 주세요.");
        mailSender.send(message);
    }

    // Cloudflare Turnstile 검증 로직
    private void verifyTurnstile(String token) {

        if (testToken.equals(token)) {
            log.info("테스트 환경에서 지정된 더미 토큰으로 Turnstile 검증을 우회합니다.");
            return;
        }

        if (token == null || token.isBlank()) {
            throw new EmailAuthException(EmailAuthErrorCode.BOT_DETECTED);
        } // 사전에 null인지 검사
        Map<String, String> body = Map.of("secret", turnstileSecret, "response", token);
        Map response = restTemplate.postForObject(turnstileVerifyUrl, body, Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
            throw new GeneralException(EmailAuthErrorCode.BOT_DETECTED);
        }
    }

    // 코드 검증 로직
    @Override
    public EmailAuthResponseDTO.VerifyResponseDTO verifyCode(EmailAuthRequestDTO.VerifyCodeDTO request) {
        EmailAuth log = emailAuthRepository.findTopByEmailOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND));
        try {
            if (log.getIsUsed()) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_ALREADY_USED); // 사용된 토큰인지 검사
            String savedCode = redisTemplate.opsForValue().get("auth:email:" + request.email());
            if (savedCode == null) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_EXPIRED); // 인증 시간 검사
            if (!savedCode.equals(request.code())) throw new EmailAuthException(EmailAuthErrorCode.INVALID_CODE); // 코드가 일치하는지 검사

            log.markAsUsed(); // 로깅

            redisTemplate.delete("auth:email:" + request.email());
            return new EmailAuthResponseDTO.VerifyResponseDTO(request.email(), LocalDateTime.now(), "SUCCESS");
        } catch (GeneralException e) {
            emailAuthLogService.recordFailure(log.getId(), e.getCode().getCode());
            throw e;
        }
    }

    // ip해싱 로직
    private String hashIp(String ip) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
}