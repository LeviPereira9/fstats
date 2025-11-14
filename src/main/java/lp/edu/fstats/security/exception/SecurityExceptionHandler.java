package lp.edu.fstats.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.response.normal.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse responseSender,
            AuthenticationException authException) throws IOException, ServletException {

        int code = HttpStatus.FORBIDDEN.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Error.ForbiddenAction")
                .message("Acesso não autorizado. Token de autenticação não foi fornecido.")
                .code(code)
                .build();

        responseSender.setContentType(MediaType.APPLICATION_JSON_VALUE);
        responseSender.setStatus(code);
        responseSender.getWriter().write(objectMapper.writeValueAsString(response));
    }
}