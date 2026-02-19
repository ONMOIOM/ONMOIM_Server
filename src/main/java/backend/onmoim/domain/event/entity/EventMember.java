package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.entity.BaseEntity;
import backend.onmoim.domain.event.enums.VoteStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "event_member")
public class EventMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    public void updateStatus(VoteStatus status) {
        this.status = status;
    }
}