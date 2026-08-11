package lp.edu.fstats.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.dto.user.UserShortResponse;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomUnauthorizedException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import lp.edu.fstats.service.user.UserService;
import lp.edu.fstats.service.verification.VerificationService;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;
    private final UserService userService;

    @Transactional
    @Override
    public AuthResponse register(AuthRegister request) {

        validateRegisterRequest(request);

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());

        User user = request.toModel(encryptedPassword);

        user = userRepository.save(user);

        String token = jwtTokenService.generateToken(user);

        verificationService.sendConfirmationEmail(user, TokenType.CONFIRMATION);

        return new AuthResponse(token);
    }

    private void validateRegisterRequest(AuthRegister request) {
        if(!request.passwordMatch()){
            throw CustomBadRequestException.passwordDontMatch();
        }

        boolean emailConflict = userRepository.existsByEmail(request.email());
        boolean usernameConflict = userRepository.existsByUsername(request.username());

        if(emailConflict){
            throw CustomDuplicateFieldException.email();
        }

        if(usernameConflict){
            throw CustomDuplicateFieldException.username();
        }

    }

    @Override
    public AuthResponse login(AuthLogin request) {
        UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                );

        try {
            Authentication auth = authenticationManager.authenticate(usernamePassword);

            String token = jwtTokenService.generateToken((User) auth.getPrincipal());

            return new AuthResponse(token);
        } catch (AuthenticationException e) {
            throw CustomUnauthorizedException.wrongCredentials();
        }
    }

    @Override
    public UserShortResponse me(){
        String username = AuthUtil.getRequester().getUsername();

        return userService.getUserShort(username);
    }
}
