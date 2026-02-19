package backend.onmoim.domain.user.repository;

import backend.onmoim.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQueryRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
