package lp.edu.fstats.config.redis.ratelimit.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.config.redis.ratelimit.service.RateLimitService;
import lp.edu.fstats.config.redis.ratelimit.snippets.RateLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor  implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if(handler instanceof HandlerMethod handlerMethod) {

            RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

            if(rateLimit != null) {
                String endpoint = request.getRequestURI();
                String clientKey = rateLimitService.getClientKey(request, endpoint);

                if(!rateLimitService
                        .isAllowed(
                                clientKey,
                                rateLimit.requests(),
                                rateLimit.time(),
                                rateLimit.unit())
                ){

                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{" +
                                    "\"error\": \"Rate limit exceeded\", " +
                                    "\"message\": \"Muitas requisições. Tente novamente em alguns minutos.\"" +
                                    "}"
                    );
                    return false;
                }
            }
        }

        return true;
    }

}
