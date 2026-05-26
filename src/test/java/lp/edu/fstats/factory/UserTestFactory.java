package lp.edu.fstats.factory;

import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserTestFactory {

    // User
    public static User buildUser(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@email.com");
        user.setPassword(new BCryptPasswordEncoder().encode("senha123"));
        user.setRole(Role.USER);
        user.setVerified(true);
        return user;
    }

    public static User buildAdmin(String username) {
        User user = buildUser(username);
        user.setRole(Role.ADMIN);
        return user;
    }

    // SecurityContext
    public static void mockAuthenticatedUser(User user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
