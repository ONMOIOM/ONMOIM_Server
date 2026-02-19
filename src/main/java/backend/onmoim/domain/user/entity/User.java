package backend.onmoim.domain.user.entity;


import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Column(name = "email" , nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "instagram_id", length = 255)
    private String instagramId;

    @Column(name = "twitter_id", length = 255)
    private String twitterId;

    @Column(name = "linkedin_id", length = 255)
    private String linkedinId;

    @Column(name = "deleted_At")
    private LocalDateTime deletedAt;

    public void withdraw() {
        this.status = Status.INACTIVE;
        this.deletedAt = LocalDateTime.now();
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = true)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    @Builder.Default
    @OneToMany(mappedBy = "host")
    private List<Event> events = new ArrayList<>();


    // 회원 정보 수정
    public void updateProfile(String nickname,
                              String introduction,
                              String instagramId,
                              String twitterId,
                              String linkedinId) {

        if (nickname != null) {
            this.nickname = nickname;
        }
        if (introduction != null) {
            this.introduction = introduction;
        }
        if (instagramId != null) {
            this.instagramId = instagramId;
        }
        if (twitterId != null) {
            this.twitterId = twitterId;
        }
        if (linkedinId != null) {
            this.linkedinId = linkedinId;
        }
    }
}