package backend.onmoim.domain.auth.controller;

import backend.onmoim.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "RefreshToken 재발급 API", description = "AccessToken 만료시 RefreshToken 재발급을 해준다.")
public interface AuthControllerDocs {

    @Operation(summary = "RefreshToken 재발급", description = "AccessToken과 RefreshToken을 재발급합니다.")
    ResponseEntity<ApiResponse<String>> refresh(
            HttpServletRequest request,
            HttpServletResponse response);
}
