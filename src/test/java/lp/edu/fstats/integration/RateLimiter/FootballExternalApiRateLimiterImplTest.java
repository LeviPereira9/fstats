package lp.edu.fstats.integration.RateLimiter;

import lp.edu.fstats.integration.service.rateLimiter.FootballExternalApiRateLimiter;
import lp.edu.fstats.integration.service.rateLimiter.Sleeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class FootballExternalApiRateLimiterImplTest {

    private Sleeper sleeper;
    private Clock fixedClock;

    private FootballExternalApiRateLimiter rateLimiter;

    private static final Instant START_TIME = Instant.parse("2024-01-01T00:00:00Z");

    @BeforeEach
    void setup(){
        this.sleeper = mock(Sleeper.class);
        this.fixedClock = Clock.fixed(START_TIME, ZoneOffset.UTC);

        this.rateLimiter = new FootballExternalApiRateLimiter(fixedClock, sleeper);
    }

    // exec dentro do limite
    @Test
    void acquire_shouldNotSleep_whenUnderRequestLimit() throws InterruptedException {
        for(int i = 0; i < 10; i++){
            rateLimiter.acquire();
        }

        verify(sleeper, never()).sleep(any());
    }

    //exec excede o limite
    @Test
    void acquire_shouldSleep_whenExceedingRequestLimit() throws InterruptedException {
        for(int i = 0; i < 11; i++){
            rateLimiter.acquire();
        }

        verify(sleeper, times(1)).sleep(Duration.ofMinutes(1));
    }

    @Test
    void acquire_shouldResetCountAfterSleep_allowingNewRequestsImmediately() throws InterruptedException {

        //Excede
        for(int i = 0; i < 11; i++){
            rateLimiter.acquire();
        }

        //deve ocorrer o reset, ent fazemos +10 req.
        for(int i = 0; i < 10; i++){
            rateLimiter.acquire();
        }

        verify(sleeper, times(1)).sleep(any());
    }

    // reset por tempo
    @Test
    void acquire_shouldResetCount_whenResetIntervalHasPassed() throws InterruptedException {

        MutableClock mutableClock = new MutableClock(START_TIME, ZoneOffset.UTC);
        FootballExternalApiRateLimiter limiter = new FootballExternalApiRateLimiter(mutableClock, sleeper);

        // 10 req
        for(int i = 0; i < 10; i++){
            limiter.acquire();
        }

        mutableClock.advance(Duration.ofMinutes(2));

        for(int i = 0; i < 10; i++){
            limiter.acquire();
        }

        // n deveria ter dormido, pois o tempo resetou a contagem
        verify(sleeper, never()).sleep(any());
    }

    @Test
    void acquire_shouldNotReset_whenResetIntervalHasNotPassed() throws InterruptedException {

        MutableClock mutableClock = new MutableClock(START_TIME, ZoneOffset.UTC);
        FootballExternalApiRateLimiter limiter = new FootballExternalApiRateLimiter(mutableClock, sleeper);

        for(int i = 0; i < 10; i++){
            limiter.acquire();
        }


        // 30 segundos, n resetou ainda
        mutableClock.advance(Duration.ofSeconds(30));

        limiter.acquire();

        verify(sleeper, times(1)).sleep(any());
    }
}
