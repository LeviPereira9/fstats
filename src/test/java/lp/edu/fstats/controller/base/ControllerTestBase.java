package lp.edu.fstats.controller.base;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.security.config.SecurityConfiguration;
import lp.edu.fstats.security.jwt.service.AuthorizationService;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest
@Import(SecurityConfiguration.class)
public abstract class ControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenService jwtTokenService;

    @Autowired
    protected AuthorizationService authorizationService;

    protected User buildAuthUser(String username, Role role) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@email.com");
        user.setPassword("senha123");
        user.setRole(role);
        user.setVerified(true);
        return user;
    }

    protected void mockAuthentication(User user) {
        when(jwtTokenService.verifyToken(any())).thenAnswer(invocation -> {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return user;
        });
    }

    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder request) {
        return request.cookie(new Cookie("access_token", "fake-token"));
    }

    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder request, String body) {
        return request
                .cookie(new Cookie("access_token", "fake-token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

}
