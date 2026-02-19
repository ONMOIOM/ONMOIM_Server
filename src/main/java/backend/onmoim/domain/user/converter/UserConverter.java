package backend.onmoim.domain.user.converter;

import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.dto.res.UserProfileDTO;
import backend.onmoim.domain.user.entity.User;

public class UserConverter {

    // Entity -> DTO
    public static SignUpResponseDTO.SignUpDTO toSignUpDTO(
            User user
    ){
        return SignUpResponseDTO.SignUpDTO.builder()
                .userId(user.getId())
                .build();
    }

    public static LoginResponseDTO.LoginDTO toLoginDTO(User user, String accessToken) {
        return LoginResponseDTO.LoginDTO.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .build();
    }

    public static UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .instagramId(user.getInstagramId())
                .twitterId(user.getTwitterId())
                .linkedinId(user.getLinkedinId())
                .build();
    }

}