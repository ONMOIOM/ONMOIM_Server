package backend.onmoim.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RotateTokenResponseDTO {
    private String newAccessToken;
    private String newRefreshToken;
}