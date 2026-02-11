package backend.onmoim.domain.user.controller;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.req.UserProfileUpdateDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.dto.res.UserProfileDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 API", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    ApiResponse<SignUpResponseDTO.SignUpDTO> signUp(
            @RequestBody @Valid SignUpRequestDTO.SignUpDTO dto
    );

    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    ApiResponse<LoginResponseDTO.LoginDTO> login(
            @RequestBody @Valid LoginRequestDTO.LoginDTO dto,
            HttpServletResponse response
    );

    @Operation(summary = "회원 조회", description = "가입된 사용자 정보를 조회합니다.")
    ApiResponse<UserProfileDTO> getProfile(@AuthenticationPrincipal User user, @PathVariable Long userId);
  
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 처리합니다.")
    ApiResponse<Void> withdraw(
            @AuthenticationPrincipal User user
    );

    @Operation(summary = "내 프로필 수정", description = "닉네임, 소개말, 소셜링크 부분 업데이트 / 변경하지 않을 필드는 null 또는 생략 / 본인 정보만 수정 가능")
    ApiResponse<UserProfileDTO> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserProfileUpdateDTO dto
    );

    @Operation(summary = "프로필 이미지 변경",
            description = "프로필 이미지만 변경합니다. 이미지 파일은 10MB 이하의 JPG, PNG, GIF만 지원합니다."
    )
    ApiResponse<String> updateProfileImage(
            @AuthenticationPrincipal User user,
            @Parameter(description = "업로드할 이미지 파일 (최대 10MB, 이미지 형식만)", required = true)
            @RequestParam("image") MultipartFile image
    );
}
