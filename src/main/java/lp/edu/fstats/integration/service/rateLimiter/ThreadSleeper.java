package lp.edu.fstats.integration.service.rateLimiter;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ThreadSleeper implements Sleeper{

    @Override
    public void sleep(Duration duration) throws InterruptedException {
        Thread.sleep(duration.toMillis());
    }
}
