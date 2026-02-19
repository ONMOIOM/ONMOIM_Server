package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailAuthLogService {
    private final EmailAuthRepository emailAuthRepository;

    // 메인 트랜잭션이 롤백되어도 이 기록이 남도록 REQUIRES_NEW 전파옵션 사용
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Long authId, String errorCode) {
        emailAuthRepository.findById(authId)
                .ifPresent(auth -> auth.recordFailure(errorCode));
    }
}
