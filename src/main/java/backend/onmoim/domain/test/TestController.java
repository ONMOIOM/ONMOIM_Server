package backend.onmoim.domain.test;

import backend.onmoim.domain.auth.exception.TokenAuthErrorCode;
import backend.onmoim.domain.auth.exception.TokenAuthException;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.exception.UserErrorCode;
import backend.onmoim.domain.user.exception.UserException;
import backend.onmoim.domain.user.repository.UserRepository;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import backend.onmoim.global.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "테스트 API", description = "개발/테스트용 API")
public class TestController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "Controller is working!";
    }

    @Operation(summary = "마스터 JWT 생성", description = "특정 사용자의 JWT를 생성합니다. 개발/테스트 전용입니다.")
    @PostMapping("/test/master-jwt")
    public ApiResponse<Map<String, String>> generateMasterJwt(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("userId", user.getId().toString());
        response.put("email", user.getEmail());
        response.put("nickname", user.getNickname());
        response.put("message", "이 토큰을 Authorization 헤더에 'Bearer {token}' 형식으로 사용하세요");

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, response);
    }

    @Operation(summary = "현재 사용자 정보 확인", description = "JWT 토큰으로 현재 인증된 사용자 정보를 확인합니다.")
    @GetMapping("/test/me")
    public ApiResponse<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        
        if (!jwtUtil.isValidAccessToken(token)) {
            throw new TokenAuthException(TokenAuthErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtUtil.getId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("nickname", user.getNickname());
        response.put("status", user.getStatus());

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, response);
    }
}