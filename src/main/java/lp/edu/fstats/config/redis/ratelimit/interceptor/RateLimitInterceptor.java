package lp.edu.fstats.config.redis.ratelimit.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.config.redis.ratelimit.service.RateLimitService;
import lp.edu.fstats.config.redis.ratelimit.snippets.RateLimit;
import lp.edu.fstats.response.normal.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor  implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

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

                    this.generateErrorResponse(response);
                    return false;
                }
            }
        }

        return true;
    }

    private void generateErrorResponse(HttpServletResponse response) throws IOException {
        int code = HttpStatus.TOO_MANY_REQUESTS.value();

        Response<Void> res = Response.<Void>builder()
                .operation("Error.RateLimitExceeded")
                .code(code)
                .message("Muitas requisições. Tente novamente em alguns minutos.")
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(res));
    }

}
