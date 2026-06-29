package lp.edu.fstats.service;

import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.security.jwt.service.JwtTokenService;
import lp.edu.fstats.service.auth.AuthServiceImpl;
import lp.edu.fstats.service.verification.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

import static lp.edu.fstats.factory.UserTestFactory.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    // Helpers
    private AuthRegister buildRegisterRequest(String username, String email){
        return new AuthRegister(
                null,
                username,
                email,
                "senha123",
                "senha123",
                LocalDate.of(2000, 1, 1)
        );
    }

    // Register
    @Test
    void register_shouldReturnAuthResponse_whenRequestIsValid(){
        AuthRegister request = buildRegisterRequest("joao", "joao@email.com");

        User savedUser = buildUser("joao");

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);

        when(userRepository.existsByUsername("joao")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtTokenService.generateToken(any(User.class))).thenReturn("token123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);

        verify(userRepository).save(any(User.class));

        verify(verificationService).sendConfirmationEmail(
                (any(User.class)),
                eq(TokenType.CONFIRMATION));
    }

    @Test
    void register_shouldThrowBadRequest_whenPasswordsDontMatch(){
        AuthRegister request = new AuthRegister(
                null,
                "joao",
                "joao@email.com",
                "senha123",
                "senha113",
                LocalDate.of(2000, 1, 1)
        );

        assertThrows(CustomBadRequestException.class,
                ()-> authService.register(request));

        verifyNoInteractions(userRepository);
        verify(verificationService, never()).sendConfirmationEmail(any(), any());

    }

    @Test
    void register_shouldThrowDuplicateField_whenEmailAlreadyExists(){
        AuthRegister request = buildRegisterRequest("joao", "joao@email.com");

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);
        when(userRepository.existsByUsername("joao")).thenReturn(false);

        assertThrows(CustomDuplicateFieldException.class,
                ()-> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(verificationService, never()).sendConfirmationEmail(any(), any());
    }

    @Test
    void register_shouldThrowDuplicateField_whenUsernameAlreadyExists(){
        AuthRegister request = buildRegisterRequest("joao", "joao@email.com");

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("joao")).thenReturn(true);

        assertThrows(CustomDuplicateFieldException.class,
                ()-> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(verificationService, never()).sendConfirmationEmail(any(), any());
    }

    // Login

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid(){
        AuthLogin request = new AuthLogin("joao", "senha123");
        User user = buildUser("joao");

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(jwtTokenService.generateToken(user)).thenReturn("token123");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        verify(authenticationManager).authenticate(any());
        verify(jwtTokenService).generateToken(user);
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid(){

        AuthLogin request = new AuthLogin("joao", "senha113");

        when(authenticationManager.authenticate(any()))
                .thenThrow(
                        new BadCredentialsException("Credenciais inválidas.")
                );

        assertThrows(BadCredentialsException.class,
                ()-> authService.login(request));

        verify(jwtTokenService, never()).generateToken(any());

    }
}
