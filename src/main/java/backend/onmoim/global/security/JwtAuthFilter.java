package backend.onmoim.global.security;

import backend.onmoim.domain.auth.exception.TokenAuthErrorCode;
import backend.onmoim.domain.auth.exception.TokenAuthException;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.BaseErrorCode;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] ALLOW_PATHS = {
            "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**",
            "/v3/api-docs/**", "/webjars/**", "/swagger-resources/configuration/ui",
            "/api/v1/users/signup", "/api/v1/users/login",
            "/api/v1/auth/email/**",
            "/api/v1/auth/refresh",
            "/api/v1/test/healthcheck",
            "/api/v1/auth/**"
    };

    // Swagger 경로는 필터를 거치지 않도록 우회
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("JWT Filter check path: {}", path);

        for (String allowPath : ALLOW_PATHS) {
            if (pathMatcher.match(allowPath, path)) {
                log.debug("Skip JWT filter for path: {}", path);
                return true;
            }
        }
        return false;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String accessToken = resolveToken(request);
            if (accessToken == null) {
                log.warn("No access token provided for path: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtil.isValidAccessToken(accessToken)) {
                throw new TokenAuthException(TokenAuthErrorCode.INVALID_TOKEN);
            }
            Long userId = jwtUtil.getId(accessToken);

            User user = userQueryRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

            if (user.getStatus() != Status.ACTIVE) {
                SecurityContextHolder.clearContext();
                throw new GeneralException(GeneralErrorCode.USER_INACTIVE);
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (GeneralException e) {
            log.error("[JWT] GeneralException: {}", e.getMessage());
            handleGeneralJwtError(e.getCode(), response);
            return;
        } catch (Exception e) {
            log.error("[JWT] Internal Error: {}", e.getMessage(), e);
            handleJwtError("인증 처리 중 오류", response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Authorization Header에서 JWT 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // JWT 커스텀 예외 처리
    private void handleGeneralJwtError(BaseErrorCode errorCode, HttpServletResponse response)
            throws IOException {
        log.error("[JWT] Error: {}", errorCode.getMessage());
        ApiResponse<String> errorResponse = ApiResponse.onFailure(errorCode, null);
        setHttpServletResponse(errorCode.getStatus().value(), errorResponse, response);
    }

    // 내부 시스템 예외 처리
    private void handleJwtError(String msg, HttpServletResponse response) throws IOException {
        log.error("[JWT] Internal Exception: {}", msg);
        ApiResponse<String> errorResponse = ApiResponse.onFailure(
                GeneralErrorCode.INTERNAL_SERVER_ERROR, msg);
        setHttpServletResponse(500, errorResponse, response);
    }

    // HTTP 응답 JSON 설정
    private void setHttpServletResponse(int status, ApiResponse<?> responseBody,
                                        HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }
}