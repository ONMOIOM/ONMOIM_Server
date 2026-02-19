package backend.onmoim.global.validation.validator;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.global.validation.annotation.ExistEvent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventIdExistValidator implements ConstraintValidator<ExistEvent,Long> {

    private final EventRepository eventRepository;

    @Override
    public boolean isValid(Long values, ConstraintValidatorContext context) {
        if(values==null){
            return true;
        }


        boolean isValid = eventRepository.existsById(values);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(AnalyticsErrorCode.BAD_EVENT_ID.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
