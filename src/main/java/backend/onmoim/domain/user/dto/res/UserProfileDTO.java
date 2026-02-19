package backend.onmoim.domain.user.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private String instagramId;
    private String twitterId;
    private String linkedinId;
    private String profileImageUrl;
}