package backend.onmoim.domain.user.dto.req;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateDTO {

    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
    private String nickname;

    @Size(max = 255, message = "소개는 255자 이하여야 합니다.")
    private String introduction;

    @Size(max = 255)
    private String instagramId;

    @Size(max = 255)
    private String twitterId;

    @Size(max = 255)
    private String linkedinId;
}