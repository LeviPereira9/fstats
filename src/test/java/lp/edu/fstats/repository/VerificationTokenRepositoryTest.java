package lp.edu.fstats.repository;

import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.model.verification.VerificationToken;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.repository.verification.VerificationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VerificationTokenRepositoryTest extends RepositoryTestBase {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User buildUser(String username){
        User user = new User();

        user.setUsername(username);
        user.setEmail(username + "@email.com");
        user.setPassword("senha123");
        user.setRole(Role.USER);
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));

        return user;
    }

    private VerificationToken buildVerificationToken(User user, TokenType type){

        VerificationToken token = new VerificationToken(user, type);

        token.setToken("t-vali");
        token.setExpiresAt(LocalDate.now().atStartOfDay().plusHours(1));

        return token;
    }

    //findByUser_UsernameAndToken
    @Test
    void findByUser_UsernameAndToken_shouldReturnToken_whenUsernameAndTokenMatch(){
        User user = userRepository.save(
                this.buildUser("joao"));

        verificationTokenRepository.save(
                this.buildVerificationToken(user, TokenType.CONFIRMATION));

        Optional<VerificationToken> result = verificationTokenRepository.findByUser_UsernameAndToken("joao", "t-vali");

        assertTrue(result.isPresent());
        assertEquals("joao", result.get().getUser().getUsername());
        assertEquals(TokenType.CONFIRMATION, result.get().getType());

    }

    @Test
    void findByUser_UsernameAndToken_shouldReturnEmpty_whenUsernameDoesNotMatch(){
        User user = userRepository.save(
                this.buildUser("joao")
        );

        verificationTokenRepository.save(
                this.buildVerificationToken(user, TokenType.CONFIRMATION)
        );

        Optional<VerificationToken> result = verificationTokenRepository
                .findByUser_UsernameAndToken("outro", "t-vali");

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_UsernameAndToken_shouldReturnEmpty_whenTokenDoesNotMatch(){

        User user = userRepository.save(
                this.buildUser("joao")
        );

        verificationTokenRepository.save(
                this.buildVerificationToken(
                        user,
                        TokenType.CONFIRMATION
                )
        );

        Optional<VerificationToken> result = verificationTokenRepository
                .findByUser_UsernameAndToken(
                        "joao",
                        "t-inva"
                );

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_UsernameAndToken_shouldReturnEmpty_whenBothDoNotMatch(){

        Optional<VerificationToken> result = verificationTokenRepository
                .findByUser_UsernameAndToken("ninguem", "t-vali");

        assertFalse(result.isPresent());
    }

}
