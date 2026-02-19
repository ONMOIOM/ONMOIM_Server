package backend.onmoim.global.common.session;
import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class RedisSessionTracker {
   private static final Duration TTL = Duration.ofMinutes(50);
   private static final String KEY_PREFIX = "session:enter:";
   private final ObjectMapper objectMapper;
   private final RedisTemplate<String,String> redisTemplate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionData {
        private Long userId;
        private Long eventId;
        private LocalDateTime enterTime;
    }

   public String enter(Long userId,Long eventId){
       String sessionId = UUID.randomUUID().toString();
       SessionData data = new SessionData(userId,eventId,LocalDateTime.now(ZoneId.of("Asia/Seoul")));

       try {
           String json = objectMapper.writeValueAsString(data);
           redisTemplate.opsForValue().set(KEY_PREFIX + sessionId, json, TTL);
       } catch (JsonProcessingException e) {
           throw new GeneralException(AnalyticsErrorCode.REDIS_SAVE_FAIL);
       }

       return sessionId;
   }

   public SessionData exit(String sessionId){
       String key =KEY_PREFIX + sessionId;
       String json = redisTemplate.opsForValue().get(key);
       if(json==null){
           throw new GeneralException(AnalyticsErrorCode.REDIS_NOT_FOUND);
       }

       try{
           SessionData data = objectMapper.readValue(json, SessionData.class);
           // Redis에서 삭제
           redisTemplate.delete(key);
           return data;
       } catch (JsonProcessingException e) {
           throw new GeneralException(AnalyticsErrorCode.REDIS_DESERIALIZE_FAIL);
       }
   }
}
