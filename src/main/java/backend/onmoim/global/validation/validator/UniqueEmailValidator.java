package backend.onmoim.global.validation.validator;

import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.validation.annotation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserQueryRepository userQueryRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // null이나 빈 문자열은 다른 validation에서 처리
        if (email == null || email.trim().isEmpty()) {
            return true;
        }

        // 이메일 중복 검사
        return !userQueryRepository.findByEmail(email).isPresent();
    }
}