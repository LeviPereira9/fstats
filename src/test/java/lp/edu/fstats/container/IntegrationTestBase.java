package lp.edu.fstats.container;

import jakarta.servlet.http.Cookie;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestBase extends AbstractContainerBase {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenService jwtTokenService;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @BeforeEach
    void cleanup(){
        databaseCleaner.clean();
    }

    protected User createUser(Role role){

        User user = new User();

        user.setUsername("user_" + System.nanoTime());
        user.setEmail("user_" + System.nanoTime() + "@test.com");
        user.setPassword(ENCODER.encode("senha123"));
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
        user.setRole(role);
        user.setVerified(true);

        return userRepository.save(user);
    }

    protected HttpHeaders authHeaders(Role role){

        User user = this.createUser(role);
        String token = jwtTokenService.generateToken(user);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, "access_token=" + token);

        return headers;
    }

    protected Cookie authCookie(Role role){
        User user = this.createUser(role);
        String token = jwtTokenService.generateToken(user);

        return new Cookie("access_token", token);
    }

}
