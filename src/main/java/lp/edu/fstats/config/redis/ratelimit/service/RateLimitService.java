package lp.edu.fstats.config.redis.ratelimit.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String clientKey, int maxRequests, int timeWindowMinutes, TimeUnit timeUnit) {
        String redisKey = "rateLimit:" + clientKey;

        // Pega o número de requisições já feitas, se não tiver, cria e começa com 1.
        Long count = redisTemplate.opsForValue().increment(redisKey);

        // Se foi criado agora, adiciona a expiração.
        if(count != null && count == 1){
            redisTemplate.expire(redisKey, timeWindowMinutes, timeUnit);
        }

        return count != null && count <= maxRequests;
    }

    public String getClientKey(HttpServletRequest request, String endpoint){

        String ip = this.getClientIp(request);

        return ip + ":" + endpoint;
    }

    private String getClientIp(HttpServletRequest request){
        String xfHeader = request.getHeader("X-Forwarded-For");

        if(xfHeader != null){
            return xfHeader.split(",")[0];
        }

        return request.getRemoteAddr();
    }

}
