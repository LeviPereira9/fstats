package lp.edu.fstats.integration.service.rateLimiter;

import java.time.Duration;

public interface Sleeper {
    void sleep(Duration duration) throws InterruptedException;
}
