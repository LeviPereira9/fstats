package lp.edu.fstats.service;

import lp.edu.fstats.dto.verification.VerificationPasswordResetRequest;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.UserTestFactory;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.model.verification.VerificationToken;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.repository.verification.VerificationTokenRepository;
import lp.edu.fstats.service.email.EmailService;
import lp.edu.fstats.service.verification.VerificationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    // Helpers

    private User buildUser(String username){
        User user = UserTestFactory.buildUser(username);
        user.setVerified(false);

        return user;
    }

    private VerificationToken buildToken(User user, TokenType type){
        VerificationToken token = new VerificationToken(user, type);

        token.setToken("token-valido");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setUsed(false);

        return token;

    }

    private VerificationToken buildExpiredToken(User user, TokenType type){
        VerificationToken token = this.buildToken(user, type);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        return token;
    }

    private VerificationToken buildUsedToken(User user, TokenType type){
        VerificationToken token = this.buildToken(user, type);
        token.setUsed(true);

        return token;
    }

    // resendConfirmationEmail

    @Test
    void resendConfirmationEmail_shouldSendEmail_whenUserExistsAndNotVerified(){
        User user = this.buildUser("joao");

        when(userRepository.findByUsernameOrEmail("joao")).thenReturn(Optional.of(user));

        verificationService.resendConfirmationEmail("joao");

        verify(verificationTokenRepository).save(any(VerificationToken.class));

        verify(emailService).sendConfirmationEmail(eq(user.getEmail()), any(String.class));
    }

    @Test
    void resendConfirmationEmail_shouldThrowNotFound_whenUserDoesNotExist(){

        when(userRepository.findByUsernameOrEmail("joao")).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> verificationService.resendConfirmationEmail("joao"));

        verifyNoInteractions(emailService);
    }

    @Test
    void resendConfirmationEmail_shouldThrowBadRequest_whenUserAlreadyVerified(){
        User user = UserTestFactory.buildUser("joao");

        when(userRepository.findByUsernameOrEmail("joao"))
                .thenReturn(Optional.of(user));

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.resendConfirmationEmail("joao"));

        verifyNoInteractions(emailService);
    }

    // sendConfirmationEmail

    @Test
    void sendConfirmationEmail_shouldSaveTokenAndSendEmail(){
        User user = this.buildUser("joao");

        verificationService.sendConfirmationEmail(user, TokenType.CONFIRMATION);

        verify(verificationTokenRepository).save(any(VerificationToken.class));

        verify(emailService).sendConfirmationEmail(
                eq(user.getEmail()),
                any(String.class));
    }

    // confirmEmail

    @Test
    void confirmEmail_shouldVerifyUser_whenTokenIsValid(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildToken(user, TokenType.CONFIRMATION);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        verificationService.confirmEmail("joao", "token-valido");

        assertTrue(user.isVerified());
        assertTrue(token.isUsed());
        verify(verificationTokenRepository).save(token);
    }

    @Test
    void confirmEmail_shouldThrowNotFound_whenTokenDoesNotExist(){
        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-invalido"))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> verificationService.confirmEmail("joao", "token-invalido"));

    }

    @Test
    void confirmEmail_shouldThrowBadRequest_whenTokenIsExpired(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildExpiredToken(user, TokenType.CONFIRMATION);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.confirmEmail("joao", "token-valido"));

        assertFalse(user.isVerified());
    }

    @Test
    void confirmEmail_shouldThrowBadRequest_whenTokenIsAlreadyUsed(){
        User user = this.buildUser("joao");
        VerificationToken token = buildUsedToken(user, TokenType.CONFIRMATION);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.confirmEmail("joao", "token-valido"));

        assertFalse(user.isVerified());
    }

    @Test
    void confirmEmail_shouldThrowBadRequest_whenTokenTypeIsWrong(){
        User user = this.buildUser("joao");
        VerificationToken token = buildToken(user, TokenType.PASSWORD);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        assertThrows(CustomBadRequestException.class,
                () -> verificationService.confirmEmail("joao", "token-valido"));

        assertFalse(user.isVerified());
    }

    // sendForgotPasswordEmail (por username)

    @Test
    void sendForgotPasswordEmail_shouldSendEmail_whenUserExists(){
        User user = this.buildUser("joao");

        when(userRepository.findByUsernameOrEmail("joao"))
                .thenReturn(Optional.of(user));

        verificationService.sendForgotPasswordEmail("joao");

        verify(verificationTokenRepository).save(any(VerificationToken.class));

        verify(emailService).sendForgotPasswordEmail(eq(user.getEmail()), any(String.class));
    }

    @Test
    void sendForgotPasswordEmail_shouldThrowNotFound_whenUserDoesNotExist(){
        when(userRepository.findByUsernameOrEmail("joao"))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> verificationService.sendForgotPasswordEmail("joao"));

        verifyNoInteractions(emailService);
    }

    // sendForgotPasswordEmail (por email)
    @Test
    void sendForgotPasswordEmail_shouldSaveTokenAndSendEmail_whenCalledWithUser(){
        User user = this.buildUser("joao");

        verificationService.sendForgotPasswordEmail(user, TokenType.PASSWORD);

        verify(verificationTokenRepository).save(any(VerificationToken.class));

        verify(emailService).sendForgotPasswordEmail(eq(user.getEmail()), any(String.class));
    }

    // resetPassword
    @Test
    void resetPassword_shouldResetPassword_whenRequestIsValid(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildToken(user, TokenType.PASSWORD);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        VerificationPasswordResetRequest request = new VerificationPasswordResetRequest(
                "novaSenha123",
                "novaSenha123"
        );

        verificationService.resetPassword("joao", "token-valido", request);

        assertTrue(token.isUsed());
        verify(userRepository).save(user);
        verify(verificationTokenRepository).save(token);

        verify(emailService).sendPasswordChangedNotification(user.getEmail());
    }

    @Test
    void resetPassword_shouldThrowBadRequest_whenPasswordsDontMatch(){
        User user = this.buildUser("joao");

        VerificationToken token = buildToken(user, TokenType.PASSWORD);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        VerificationPasswordResetRequest request = new VerificationPasswordResetRequest(
                "novaSenha123",
                "novaSenha113"
        );

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.resetPassword("joao", "token-valido", request));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendPasswordChangedNotification(user.getEmail());
    }

    @Test
    void resetPassword_shouldThrowBadRequest_whenTokenIsExpired(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildExpiredToken(user, TokenType.PASSWORD);

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        VerificationPasswordResetRequest request = new VerificationPasswordResetRequest(
                "novaSenha123",
                "novaSenha123"
        );

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.resetPassword("joao", "token-valido", request));

        verify(userRepository, never()).save(any());
    }

    // sendEmailChangeConfirmation

    @Test
    void sendEmailChangeConfirmation_shouldSaveTokenAndSendEmail(){
        User user = this.buildUser("joao");

        verificationService.sendEmailChangeConfirmation(user, "novo@email.com");

        verify(verificationTokenRepository).save(any(VerificationToken.class));

        verify(emailService).sendEmailChangeConfirmation(
                eq(user.getEmail()),
                any(String.class));
    }

    // confirmEmailChange

    @Test
    void confirmEmailChange_shouldChangeEmail_whenTokenIsValid(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildToken(user, TokenType.EMAIL_CHANGE);
        token.setContext("novo@email.com");

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        verificationService.confirmEmailChange("joao", "token-valido");

        assertEquals("novo@email.com", user.getEmail());
        assertTrue(token.isUsed());

        verify(userRepository).save(user);
        verify(verificationTokenRepository).save(token);

        verify(emailService).sendEmailChangedNotification("novo@email.com", "joao@email.com");
    }

    @Test
    void confirmEmailChange_shouldThrowBadRequest_whenTokenIsExpired(){
        User user = this.buildUser("joao");
        VerificationToken token = this.buildExpiredToken(user, TokenType.EMAIL_CHANGE);
        token.setContext("novo@email.com");

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.confirmEmailChange(
                        "joao", "token-valido"));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendEmailChangedNotification("novo@email.com", "joao@email.com");
    }

    @Test
    void confirmEmailChange_shouldThrowBadRequest_whenTokenIsAlreadyUsed(){

        User user = this.buildUser("joao");
        VerificationToken token = this.buildUsedToken(user, TokenType.EMAIL_CHANGE);
        token.setContext("novo@email.com");

        when(verificationTokenRepository.findByUser_UsernameAndToken("joao", "token-valido"))
                .thenReturn(Optional.of(token));

        assertThrows(CustomBadRequestException.class,
                ()-> verificationService.confirmEmailChange("joao", "token-valido"));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendEmailChangedNotification("novo@email.com", "joao@email.com");
    }

    // sendPasswordChangedNotification

    @Test
    void sendPasswordChangedNotification_shouldDelegateToEmailService(){

        verificationService.sendPasswordChangedNotification("joao@email.com");

        verify(emailService).sendPasswordChangedNotification("joao@email.com");

    }
}
