package lp.edu.fstats.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomInternalServerError;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenService jwtTokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        return Arrays.stream(
                AuthUtil.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = this.recoverToken(request);

            User userDetails = jwtTokenService.verifyToken(token);

            if (userDetails == null) {
                return;
            }

            filterChain.doFilter(request, response);

        } catch (CustomForbiddenActionException e) {
            this.generateErrorResponse(response, "Error.Unauthorized", 401, "Usuário não autenticado.", null);

        } catch (CustomInternalServerError e) {
            this.generateErrorResponse(response, "Error.InternalServer", 503, e.getMessage(), null);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String recoverToken(HttpServletRequest request) {

        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("access_token".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }

        throw CustomForbiddenActionException.notAuthorized();
    }

    private void generateErrorResponse(HttpServletResponse responseSender, String operation, int code, String message, Map<String, String> fieldErrors) throws IOException {

        Response<Void> response = Response.<Void>builder()
                .operation(operation)
                .code(code)
                .message(message)
                .fieldErrors(fieldErrors)
                .build();

        responseSender.setContentType(MediaType.APPLICATION_JSON_VALUE);
        responseSender.setStatus(code);
        responseSender.getWriter().write(objectMapper.writeValueAsString(response));
    }
}