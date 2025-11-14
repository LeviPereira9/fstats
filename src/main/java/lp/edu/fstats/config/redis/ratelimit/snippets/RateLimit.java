package lp.edu.fstats.config.redis.ratelimit.snippets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requests() default 10;
    int time() default 1;
    TimeUnit unit() default TimeUnit.MINUTES;
}