package backend.onmoim.domain.auth.controller;

import backend.onmoim.domain.auth.dto.response.RotateTokenResponseDTO;
import backend.onmoim.domain.auth.service.AuthService;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import backend.onmoim.global.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDocs {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = jwtUtil.getRefreshTokenCookie(request);
        RotateTokenResponseDTO tokens = authService.rotateAccessToken(refreshToken);

        jwtUtil.setRefreshTokenCookie(response, tokens.getNewRefreshToken());

        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, tokens.getNewAccessToken()));
    }
}