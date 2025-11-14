package lp.edu.fstats.service.verification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.verification.VerificationPasswordResetRequest;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;
import lp.edu.fstats.model.verification.VerificationToken;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.repository.verification.VerificationTokenRepository;
import lp.edu.fstats.service.email.EmailService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public void resendConfirmationEmail(String login) {
        User user = userRepository.findByUsernameOrEmail(login)
                .orElseThrow(CustomNotFoundException::user);

        if(user.isVerified()){
            throw CustomBadRequestException.userAlreadyVerified();
        }

        this.sendConfirmationEmail(user, TokenType.CONFIRMATION);
    }

    @Override
    public void sendConfirmationEmail(User user, TokenType tokenType) {
        VerificationToken verificationToken = new VerificationToken(user, tokenType);

        verificationTokenRepository.save(verificationToken);

        emailService.sendConfirmationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Transactional
    @Override
    public void confirmEmail(String username, String token) {
        VerificationToken verificationToken = this.findVerificationToken(username, token, TokenType.CONFIRMATION);
        verificationToken.setUsed(true);

        User user = verificationToken.getUser();
        user.verify();
        user.update(user);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void sendForgotPasswordEmail(String login) {
        User user = userRepository.findByUsernameOrEmail(login)
                .orElseThrow(CustomNotFoundException::user);

        this.sendForgotPasswordEmail(user, TokenType.PASSWORD);
    }

    @Override
    public void sendForgotPasswordEmail(User user, TokenType tokenType) {
        VerificationToken verificationToken = new VerificationToken(user, tokenType);
        verificationTokenRepository.save(verificationToken);

        emailService.sendForgotPasswordEmail(user.getEmail(), verificationToken.getToken());
    }

    @Transactional
    @Override
    public void resetPassword(String username, String token, VerificationPasswordResetRequest request) {
        VerificationToken verificationToken = this.findVerificationToken(username, token, TokenType.PASSWORD);
        verificationToken.setUsed(true);

        User user = verificationToken.getUser();

        if(!request.passwordMatches()){
            throw CustomBadRequestException.passwordDontMatch();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.newPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);

        emailService.sendPasswordChangedNotification(user.getEmail());
    }

    @Override
    public void sendEmailChangeConfirmation(User user, String newEmail) {
        VerificationToken verificationToken = new VerificationToken(user, TokenType.EMAIL_CHANGE);
        verificationToken.setContext(newEmail);

        verificationTokenRepository.save(verificationToken);

        emailService.sendEmailChangeConfirmation(user.getEmail(), verificationToken.getToken());
    }

    @Transactional
    @Override
    public void confirmEmailChange(String username, String token) {
        VerificationToken verificationToken = this.findVerificationToken(username, token, TokenType.EMAIL_CHANGE);
        verificationToken.setUsed(true);

        User user = verificationToken.getUser();

        String newEmail = verificationToken.getContext();
        String oldEmail = user.getEmail();

        user.update(user);
        user.setEmail(verificationToken.getContext());

        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);

        emailService.sendEmailChangedNotification(newEmail, oldEmail);
    }

    @Override
    public void sendPasswordChangedNotification(String email) {
        emailService.sendPasswordChangedNotification(email);
    }

    private VerificationToken findVerificationToken(String username, String token, TokenType type) {
        VerificationToken verificationToken = verificationTokenRepository.findByUser_UsernameAndToken(username, token)
                .orElseThrow(CustomNotFoundException::verificationTokenEmail);

        if (verificationToken.isExpired()) {
            throw CustomBadRequestException.verificationTokenExpired();
        }

        if (verificationToken.isUsed()) {
            throw CustomBadRequestException.verificationTokenUsed();
        }

        if(verificationToken.getType() != type){
            throw CustomBadRequestException.verificationTokenType();
        }

        return verificationToken;
    }
}
