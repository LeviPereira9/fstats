package lp.edu.fstats.support;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

@Component
@RequiredArgsConstructor
@ActiveProfiles("integration")
public class TestRedisCleaner {

    private final StringRedisTemplate redisTemplate;

    public void clearAll(){
        Set<String> keys = redisTemplate.keys("*");

        if(keys != null && !keys.isEmpty()){
            redisTemplate.delete(keys);
        }
    }
}
