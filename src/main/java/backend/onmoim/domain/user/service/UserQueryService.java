package backend.onmoim.domain.user.service;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.req.UserProfileUpdateDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.dto.res.UserProfileDTO;
import backend.onmoim.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

public interface UserQueryService {
    LoginResponseDTO.LoginDTO login(@Valid LoginRequestDTO.LoginDTO dto, HttpServletResponse response);

    // 회원가입
    @Transactional
    SignUpResponseDTO.SignUpDTO signup(SignUpRequestDTO.SignUpDTO dto);

    @Transactional
    UserProfileDTO getProfile(@AuthenticationPrincipal User user, Long userId);

    @Transactional
    UserProfileDTO updateMyProfile(@AuthenticationPrincipal User loginUser, UserProfileUpdateDTO dto);

    @Transactional
    String updateProfileImage(User user, MultipartFile image);
}
