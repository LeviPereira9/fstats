package lp.edu.fstats.security.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lp.edu.fstats.exception.custom.CustomInternalServerError;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.security.jwt.dto.TokenPayload;
import lp.edu.fstats.util.ExpirationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final AuthorizationService authorizationService;
    @Value("${api.security.token.secret}")
    private String secret;

    public JwtTokenService(AuthorizationService authorizationService) {this.authorizationService = authorizationService;}

    public String generateToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String jwtVersion = user.getTokenVersion();

            return JWT.create()
                    .withIssuer("fstats-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(ExpirationUtil.defaultJwtExpirationTime())
                    .withClaim("jwtVersion", jwtVersion)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw CustomInternalServerError.tokenCreation();
        }
    }

    public TokenPayload validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedjwt = JWT
                    .require(algorithm)
                    .withIssuer("fstats-api")
                    .build()
                    .verify(token);

            String username = decodedjwt.getSubject();
            String jwtVersion = decodedjwt.getClaim("jwtVersion").asString();

            return new TokenPayload(username, jwtVersion);
        } catch (JWTVerificationException e){
            throw CustomInternalServerError.tokenValidation();
        }
    }


    public User verifyToken(String token){
        TokenPayload payload = this.validateToken(token);

        User user = authorizationService.loadUserByUsername(payload.username());

        if(!user.getTokenVersion().equals(payload.tokenVersion())) {
            throw CustomInternalServerError.tokenValidation();
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }
}
