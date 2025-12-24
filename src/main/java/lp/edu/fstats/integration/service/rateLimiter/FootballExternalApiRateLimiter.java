package lp.edu.fstats.integration.service.rateLimiter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class FootballExternalApiRateLimiter implements RateLimiter {

    private final AtomicInteger REQUEST_COUNT = new AtomicInteger(0);
    private final int MAX_REQUESTS = 10;
    private final Duration RESET_INTERVAL = Duration.ofMinutes(1);
    private Instant LAST_RESET = Instant.now();

    @Override
    public synchronized void acquire() {
        this.resetIfNeeded();

        if(REQUEST_COUNT.incrementAndGet() > MAX_REQUESTS) {
            this.sleepUntilReset();
            this.reset();
        }

    }

    private void resetIfNeeded() {
        if(Instant.now().isAfter(LAST_RESET.plus(RESET_INTERVAL))) {
            this.reset();
        }
    }

    private void reset() {
        REQUEST_COUNT.set(0);
        LAST_RESET = Instant.now();
    }

    private void sleepUntilReset() {
        try {
            Thread.sleep(RESET_INTERVAL.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
