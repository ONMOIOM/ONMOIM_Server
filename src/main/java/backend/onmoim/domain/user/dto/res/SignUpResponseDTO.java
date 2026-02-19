package backend.onmoim.domain.user.dto.res;

import lombok.Builder;

public class SignUpResponseDTO {

    @Builder
    public record SignUpDTO(
            Long userId
    ){}
}
