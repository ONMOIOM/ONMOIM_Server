package backend.onmoim.domain.event.dto.req; // 👈 패키지 경로에 .req 추가

import backend.onmoim.domain.event.enums.VoteStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoteRequest {
    private VoteStatus status;
}