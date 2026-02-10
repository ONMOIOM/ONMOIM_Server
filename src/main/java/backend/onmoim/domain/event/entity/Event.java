package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.event.enums.Status;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event extends BaseEntity {
    @Column(name = "event_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = true)
    private String title;

    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @Column(name = "street_address", nullable = true, length = 255)
    private String streetAddress;

    @Column(name = "lot_number_address", nullable = true, length = 255)
    private String lotNumberAddress;

    @Column(name = "price", nullable = true)
    private Integer price;

    @Column(name = "capacity", nullable = true)
    private Integer capacity;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "status", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "playlist_url", nullable = true)
    private String playlistUrl;

    @OneToMany(mappedBy = "event")
    private List<Analytics> analytics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User host;

    public Event update(String title, LocalDateTime startTime,
                        LocalDateTime endTime, String streetAddress,
                        String lotNumberAddress, Integer price,
                        String playlistUrl, Integer capacity,String introduction) {
        return Event.builder()
                .id(this.id)
                .title(title != null ? title : this.title)
                .startTime(startTime != null ? startTime : this.startTime)
                .endTime(endTime != null ? endTime : this.endTime)
                .streetAddress(streetAddress != null ? streetAddress : this.streetAddress)
                .lotNumberAddress(lotNumberAddress != null ? lotNumberAddress : this.lotNumberAddress)
                .price(price != null ? price : this.price)
                .capacity(capacity != null ? capacity : this.capacity)
                .introduction(introduction != null ? introduction : this.introduction)
                .status(this.status)
                .playlistUrl(playlistUrl != null ? playlistUrl: this.playlistUrl)
                .build();
    }

    public Event publish() {
        return Event.builder()
                .id(this.id)
                .title(this.title)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .streetAddress(this.streetAddress)
                .lotNumberAddress(this.lotNumberAddress)
                .price(this.price)
                .capacity(this.capacity)
                .introduction(this.introduction)
                .status(Status.PUBLISHED)
                .playlistUrl(this.playlistUrl)
                .host(this.host)
                .build();
    }
}