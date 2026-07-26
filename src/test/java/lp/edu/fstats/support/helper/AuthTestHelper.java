package lp.edu.fstats.support.helper;

import io.restassured.http.Cookie;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AuthTestHelper {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(String username, String email, String rawPassword, Role role){
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        user.setPassword(passwordEncoder.encode(rawPassword));

        user.setDateOfBirth(LocalDate.now().minusYears(20));

        user.setRole(role);
        user.setVerified(true);

        return userRepository.save(user);
    }

    public User createDefaultUser(String rawPassword){

        return this.createUser(
                "user_test",
                "user_test@test.com",
                rawPassword,
                Role.USER
                );

    }

    public User notVerifiedUser(String username, String rawPassword){
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");

        user.setPassword(passwordEncoder.encode(rawPassword));

        user.setDateOfBirth(LocalDate.now().minusYears(20));

        user.setRole(Role.USER);
        user.setVerified(false);

        return userRepository.save(user);
    }

    public Cookie authCookie(User user){
        String token = jwtTokenService.generateToken(user);

        return new Cookie.Builder("access_token", token)
                .setPath("/")
                .build();
    }

}
