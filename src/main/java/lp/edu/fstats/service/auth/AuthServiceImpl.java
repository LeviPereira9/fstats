package lp.edu.fstats.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import lp.edu.fstats.service.verification.VerificationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;

    @Transactional
    @Override
    public AuthResponse register(AuthRegister request) {

        validateRegisterRequest(request);

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());

        User user = request.toModel(encryptedPassword);

        user = userRepository.save(user);

        String token = "Bearer " + jwtTokenService.generateToken(user);

        verificationService.sendConfirmationEmail(user, TokenType.CONFIRMATION);

        return new AuthResponse(token);
    }

    private void validateRegisterRequest(AuthRegister request) {
        boolean emailConflict = userRepository.existsByEmail(request.email());
        boolean usernameConflict = userRepository.existsByUsername(request.email());

        if(!request.passwordMatch()){
            throw CustomBadRequestException.passwordDontMatch();
        }

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

        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = "Bearer " + jwtTokenService.generateToken((User) auth.getPrincipal());

        return new AuthResponse(token);
    }
}
