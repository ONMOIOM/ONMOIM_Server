package backend.onmoim.domain.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteStatus {
    ATTEND("참여!"),
    PENDING("고민중.."),
    ABSENT("못가요..");

    private final String description;
}