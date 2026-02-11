package backend.onmoim.domain.user.service;

import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.service.command.EmailAuthCommandService;
import backend.onmoim.domain.user.converter.UserConverter;
import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.req.UserProfileUpdateDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.dto.res.UserProfileDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.domain.user.repository.UserRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.JwtUtil;
import backend.onmoim.global.utils.MinioUtil;
import backend.onmoim.global.utils.RandomNicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final EmailAuthCommandService emailAuthCommandService;
    private final MinioUtil minioUtil;

    @Override
    public LoginResponseDTO.LoginDTO login(
            LoginRequestDTO.@Valid LoginDTO dto,
            HttpServletResponse response
    ) {

        // User 조회
        User user = userQueryRepository.findByEmail(dto.email())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            throw new GeneralException(GeneralErrorCode.USER_INACTIVE);
        }

        // 이메일 인증코드 검증
        emailAuthCommandService.verifyCode(
                new EmailAuthRequestDTO.VerifyCodeDTO(dto.email(), dto.authCode())
        );

        // 엑세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        jwtUtil.setRefreshTokenCookie(response, refreshToken);
        String refreshKey = "refresh:token:" + user.getId();
        jwtUtil.storeRefreshToken(refreshKey, refreshToken);

        // DTO 조립
        return UserConverter.toLoginDTO(user, accessToken);
    }

    // 회원가입
    @Transactional
    @Override
    public SignUpResponseDTO.SignUpDTO signup(SignUpRequestDTO.SignUpDTO dto) {

        // 먼저 이메일 인증 검증 (존재 여부 노출 방지)
        try {
            emailAuthCommandService.verifyCode(
                    new EmailAuthRequestDTO.VerifyCodeDTO(dto.email(), dto.authCode())
            );
        } catch (EmailAuthException e) {
            throw new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND);
        }

        String randomNickname = randomNicknameGenerator.generateUniqueNickname();

        User user = User.builder()
                .email(dto.email())
                .nickname(randomNickname)
                .status(Status.ACTIVE)
                .build();

        try {
            userRepository.save(user);
            return UserConverter.toSignUpDTO(user);
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_MEMBER);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(@AuthenticationPrincipal User user, Long userId) {
        if (user.getStatus() != Status.ACTIVE) {
            throw new GeneralException(GeneralErrorCode.USER_INACTIVE);
        }

        User userprofile = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        UserProfileDTO dto = UserConverter.toProfileDTO(userprofile);
        String imageUrl = null;
        try {
                imageUrl = minioUtil.getProfileImageUrl(userId);
            } catch (Exception e) {
                log.warn("프로필 이미지 URL 생성 실패: {}", e.getMessage(), e);
            }
        dto.setProfileImageUrl(imageUrl);

        return dto;
    }

    @Transactional
    @Override
    public UserProfileDTO updateMyProfile(@AuthenticationPrincipal User loginUser, UserProfileUpdateDTO dto) {

        User user = userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            throw new GeneralException(GeneralErrorCode.USER_INACTIVE);
        }

        // 엔티티 메서드 호출 (null 자동 무시)
        user.updateProfile(
                dto.getNickname(),
                dto.getIntroduction(),
                dto.getInstagramId(),
                dto.getTwitterId(),
                dto.getLinkedinId()
        );

        return UserConverter.toProfileDTO(user);
    }

    @Transactional
    @Override
    public String updateProfileImage(User user, MultipartFile image) {
        validateImage(image);

        try {
            minioUtil.uploadProfileImage(image, user.getId());

            return minioUtil.getProfileImageUrl(user.getId());

        } catch (Exception e) {
            log.error("이미지 업로드 실패: {}", e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }

    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE);
        }

        long size = image.getSize();
        if (size > 10 * 1024 * 1024) { // 10MB 제한
            throw new GeneralException(GeneralErrorCode.IMAGE_SIZE_EXCEEDED);
        }

        // 파일 형식 검증
        String contentType = image.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    private boolean isAllowedImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif");
        }
}