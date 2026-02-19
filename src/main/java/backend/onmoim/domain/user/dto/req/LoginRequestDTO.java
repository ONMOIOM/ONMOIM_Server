package backend.onmoim.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class LoginRequestDTO {

    @Builder
    public record LoginDTO(
            @Schema(description = "이메일", example = "test@example.com")
            @NotBlank
            @Email
            String email,
            
            @Schema(description = "인증코드", example = "123456")
            @NotBlank
            String authCode
    ){}
}
