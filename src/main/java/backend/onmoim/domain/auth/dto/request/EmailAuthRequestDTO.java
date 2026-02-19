package backend.onmoim.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public class EmailAuthRequestDTO {
    public record SendCodeDTO(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "보안 토큰이 누락되었습니다.")
            String turnstileToken
    ) {}

    public record VerifyCodeDTO(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "인증 코드는 필수 입력값입니다.")
            String code
    ) {}
}
