package lp.edu.fstats.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lp.edu.fstats.exception.custom.CustomInternalServerError;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.entity.UserTestFactory;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.security.jwt.dto.TokenPayload;
import lp.edu.fstats.security.jwt.service.AuthorizationService;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private static final String SECRET = "minha-chave-secreta-de-teste-123456";

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(jwtTokenService, "secret", SECRET);
    }

    @AfterEach
    void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }

    //helpers
    private User buildUser(String username, String tokenVersion){
        User user = UserTestFactory.buildUser(username);

        user.setTokenVersion(tokenVersion);

        return user;
    }


    //generateToken
    @Test
    void generateToken_shouldReturnValidJwt_withCorrectClaims(){
        User user = this.buildUser("joao", "v1");

        String token = jwtTokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        // decodifica o token gerado para confirmas os claims, sem usar o métodu valdiateToken.

        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        DecodedJWT decoded = JWT.require(algorithm)
                .withIssuer("fstats-api")
                .build()
                .verify(token);

        assertEquals("joao", decoded.getSubject());
        assertEquals("v1", decoded.getClaim("jwtVersion").asString());
    }

    @Test
    void generateToken_shouldThrowInternalServerError_whenSecretIsInvalid(){
        ReflectionTestUtils.setField(jwtTokenService, "secret", null);

        User user = this.buildUser("joao", "v1");

        assertThrows(CustomInternalServerError.class,
                ()->jwtTokenService.generateToken(user));
    }

    // validateToken

    @Test
    void validateToken_shouldReturnTokenPayload_whenTokenIsValid(){
        User user = this.buildUser("joao", "v1");

        String token = jwtTokenService.generateToken(user);

        TokenPayload payload = jwtTokenService.validateToken(token);

        assertNotNull(payload);
        assertEquals("joao", payload.username());
        assertEquals("v1", payload.tokenVersion());
    }

    @Test
    void validateToken_shouldThrowInternalInternalServerError_whenTokenIsMalformed(){
        assertThrows(CustomInternalServerError.class,
                () -> jwtTokenService.validateToken("token-invalido-qualquer"));
    }

    @Test
    void validateToken_shouldThrowInternalServerError_whenTokenHasWrongSignature(){
        User user = this.buildUser("joao", "v1");

        String token = jwtTokenService.generateToken(user);

        // troca o secret dps de gerar, simulando outra signature.

        ReflectionTestUtils.setField(jwtTokenService, "secret", "outra-chave-diferente");

        assertThrows(CustomInternalServerError.class,
                () -> jwtTokenService.validateToken(token));
    }

    @Test
    void validateToken_shouldThrowInternalServerError_whenIssuerDoesNotMatch(){

        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("outro-issuer")
                .withSubject("joao")
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .withClaim("jwtVersion", "v1")
                .sign(algorithm);

        assertThrows(CustomInternalServerError.class,
                () -> jwtTokenService.validateToken(tokenWithWrongIssuer));

    }

    @Test
    void validateToken_shouldThrowInternalServerError_whenTokenIsExpired(){
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String expiredToken = JWT.create()
                .withIssuer("fstats-api")
                .withSubject("joao")
                .withExpiresAt(Instant.now().minusSeconds(10))
                .withClaim("jwtVersion", "v1")
                .sign(algorithm);

        assertThrows(CustomInternalServerError.class,
                ()-> jwtTokenService.validateToken(expiredToken));
    }

    //verifyToken
    @Test
    void verifyToken_shouldReturnUserAndSetSecurityContext_whenTokenAndVersionMatch(){
        User user = this.buildUser("joao", "v1");

        String token = jwtTokenService.generateToken(user);

        when(authorizationService.loadUserByUsername("joao")).thenReturn(user);

        User result = jwtTokenService.verifyToken(token);

        assertNotNull(result);
        assertEquals("joao", result.getUsername());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
    }

    @Test
    void verifyToken_shouldThrowInternalServerError_whenTokenVersionDoesNotMatch(){
        User userAtGeneration = this.buildUser("joao", "v1");
        String token = jwtTokenService.generateToken(userAtGeneration);

        // o usuário no banco está com uma version diferente
        User userInDataBase = this.buildUser("joao", "v2");

        when(authorizationService.loadUserByUsername("joao")).thenReturn(userInDataBase);

        assertThrows(CustomInternalServerError.class,
                ()-> jwtTokenService.verifyToken(token));
    }

    @Test
    void verifyToken_shouldThrowNotFound_whenUserDoesNotExist(){
        User user = this.buildUser("joao", "v1");

        String token = jwtTokenService.generateToken(user);

        when(authorizationService.loadUserByUsername("joao")).thenThrow(CustomNotFoundException.user());

        assertThrows(CustomNotFoundException.class,
                ()-> jwtTokenService.verifyToken(token));
    }

}
