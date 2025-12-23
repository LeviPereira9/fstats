package lp.edu.fstats.config.redis;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lp.edu.fstats.dto.averages.AverageResponse;
import lp.edu.fstats.dto.averages.AveragesResponse;
import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.dto.match.MatchResponse;
import lp.edu.fstats.dto.match.MatchesResponse;
import lp.edu.fstats.dto.standings.StandingsResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private <T> RedisCacheConfiguration typedCache(
            ObjectMapper mapper,
            Class<T> type,
            Duration ttl
    ) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);

        Jackson2JsonRedisSerializer<T> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, javaType);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )
                .entryTtl(ttl);
    }


    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("competition",
                        typedCache(objectMapper, CompetitionResponse.class, Duration.ofMinutes(15)))
                .withCacheConfiguration("matches",
                        typedCache(objectMapper, MatchesResponse.class, Duration.ofMinutes(15)))
                .withCacheConfiguration("averages",
                        typedCache(objectMapper, AveragesResponse.class, Duration.ofMinutes(15)))
                .withCacheConfiguration("standings",
                        typedCache(objectMapper, StandingsResponse.class, Duration.ofMinutes(15)))
                .build();
    }
}
