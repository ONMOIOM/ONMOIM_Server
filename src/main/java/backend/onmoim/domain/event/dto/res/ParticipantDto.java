package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.event.enums.VoteStatus;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.utils.MinioUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private VoteStatus status;
    private String imageUrl;

    public static ParticipantDto from(EventMember member, MinioUtil minioUtil) {
        User user = member.getUser();
        String imageUrl = null;
        try {
            imageUrl = minioUtil.getProfileImageUrl(user.getId());
        } catch (Exception e) {
            // 로그는 서비스 레이어에서 처리하므로 여기서는 무시
        }

        return ParticipantDto.builder()
                .userId(member.getUser().getId())
                .nickname(member.getUser().getNickname())
                .status(member.getStatus())
                .imageUrl(imageUrl)
                .build();
    }
}