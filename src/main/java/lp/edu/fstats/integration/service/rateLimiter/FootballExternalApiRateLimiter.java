package lp.edu.fstats.integration.service.rateLimiter;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FootballExternalApiRateLimiter implements RateLimiter {

    private final AtomicInteger REQUEST_COUNT = new AtomicInteger(0);
    private final int MAX_REQUESTS = 10;
    private final Duration RESET_INTERVAL = Duration.ofMinutes(1);

    private final Clock clock;
    private final Sleeper sleeper;

    private Instant LAST_RESET;

    public FootballExternalApiRateLimiter(Clock clock, Sleeper sleeper) {
        this.clock = clock;
        this.sleeper = sleeper;

        this.LAST_RESET = clock.instant();
    }


    @Override
    public synchronized void acquire() {
        this.resetIfNeeded();

        if(REQUEST_COUNT.incrementAndGet() > MAX_REQUESTS) {
            this.sleepUntilReset();
            this.reset();
        }
    }

    private void resetIfNeeded() {
        if(clock.instant().isAfter(LAST_RESET.plus(RESET_INTERVAL))) {
            this.reset();
        }
    }

    private void reset() {
        REQUEST_COUNT.set(0);
        LAST_RESET = clock.instant();
    }

    private void sleepUntilReset() {
        try {
            sleeper.sleep(RESET_INTERVAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
