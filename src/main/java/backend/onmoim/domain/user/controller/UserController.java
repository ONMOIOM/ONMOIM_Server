package backend.onmoim.domain.user.controller;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.req.UserProfileUpdateDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.dto.res.UserProfileDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.service.UserCommandService;
import backend.onmoim.domain.user.service.UserQueryService;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;


    @Override
    @PostMapping("/signup")
    public ApiResponse<SignUpResponseDTO.SignUpDTO> signUp(@RequestBody @Valid SignUpRequestDTO.SignUpDTO dto) {
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,userQueryService.signup(dto));
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO.LoginDTO> login(
            @RequestBody @Valid LoginRequestDTO.LoginDTO dto,
            HttpServletResponse response
    ){
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, userQueryService.login(dto,response));
    }

    @Override
    @GetMapping("/{userId}")
    public ApiResponse<UserProfileDTO> getProfile(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") Long userId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, userQueryService.getProfile(user, userId));
    }
      
    @Override
    @DeleteMapping("")
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal User user
    ) {
        userCommandService.withdraw(user.getId());
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,null);
    }

    @Override
    @PatchMapping("")
    public ApiResponse<UserProfileDTO> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserProfileUpdateDTO dto) {

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,
                userQueryService.updateMyProfile(user, dto));
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("image") MultipartFile image
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, userQueryService.updateProfileImage(user, image));
    }

}
