package backend.onmoim.domain.auth.entity;

import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailAuth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private Boolean isUsed;

    private String failureReasonCode; // 실패사유 - 에러코드 적용

    private String hashedIp; // 로깅용  해싱된 ip

    private LocalDateTime verifiedAt;

    // 업데이트 메서드
    // 인증된 시간 + 사용상태 갱신
    public void markAsUsed() {
        this.isUsed = true;
        this.verifiedAt = LocalDateTime.now();
    }

    //에러코드
    public void recordFailure(String errorCode) {
        this.failureReasonCode = errorCode;
    }
}
