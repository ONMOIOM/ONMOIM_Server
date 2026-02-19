package backend.onmoim.domain.user.dto.res;

import lombok.Builder;

public class LoginResponseDTO {

    @Builder
    public record LoginDTO(
            Long userId,
            String accessToken
    ){}
}
