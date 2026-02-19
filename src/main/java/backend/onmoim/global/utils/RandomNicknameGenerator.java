package backend.onmoim.global.utils;

import backend.onmoim.domain.user.exception.UserErrorCode;
import backend.onmoim.domain.user.exception.UserException;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomNicknameGenerator {

    private static final String[] ADJECTIVES = {
            "행복한", "빠른", "멋진", "신나는", "따뜻한", "밝은", "시원한", "강한",
            "부드러운", "재치있는", "용감한", "자유로운", "평화로운", "신비로운"
    };

    private static final String[] NOUNS = {
            "고양이", "강아지", "여우", "토끼", "곰", "펭귄", "판다", "늑대",
            "새", "물고기", "나비", "꿀벌", "달", "별", "바람", "파도"
    };

    private final UserQueryRepository userQueryRepository;
    private final Random random = new Random();

    public RandomNicknameGenerator(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    public String generateUniqueNickname() {
        int maxTries = 10;
        for (int i = 0; i < maxTries; i++) {
            String nickname = createRandomNickname();
            if (isNicknameAvailable(nickname)) {
                return nickname;
            }
        }
        throw new UserException(UserErrorCode.NICKNAME_GENERATION_FAILED);
    }

    private String createRandomNickname() {
        String adj = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = random.nextInt(100);
        return String.format("%s%s%d", adj, noun, number);
    }

    private boolean isNicknameAvailable(String nickname) {
        return !userQueryRepository.existsByNickname(nickname);
    }
}
