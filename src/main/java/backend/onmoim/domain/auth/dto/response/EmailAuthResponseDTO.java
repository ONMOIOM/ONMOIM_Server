package backend.onmoim.domain.auth.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class EmailAuthResponseDTO {
    // 발송 결과 응답
    public record VerificationResultDTO(
            String email,
            LocalDateTime sentAt,
            Long expiresInSeconds,
            boolean isRegistered
    ) {}

    public record VerifyResponseDTO(
            String email,
            LocalDateTime verifiedAt,
            String status
    ) {}

}
