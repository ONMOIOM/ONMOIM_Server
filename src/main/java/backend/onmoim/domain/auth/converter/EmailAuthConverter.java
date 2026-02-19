package backend.onmoim.domain.auth.converter;

import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;

import java.time.LocalDateTime;

public class EmailAuthConverter {
    public static EmailAuth toEmailAuth(String email, String code) {
        return EmailAuth.builder()
                .email(email)
                .code(code)
                .isUsed(false)
                .build();
    }
    public static EmailAuthResponseDTO.VerificationResultDTO toResultDTO(String email, Long expireSeconds,boolean isRegistered) {
        return new EmailAuthResponseDTO.VerificationResultDTO(
                email,
                LocalDateTime.now(),
                expireSeconds,
                isRegistered
        );
    }
}
