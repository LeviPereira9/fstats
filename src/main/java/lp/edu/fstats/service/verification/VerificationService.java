package lp.edu.fstats.service.verification;

import lp.edu.fstats.dto.verification.VerificationPasswordResetRequest;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.model.verification.TokenType;

public interface VerificationService {
    void resendConfirmationEmail(String email);

    void sendConfirmationEmail(User user, TokenType tokenType);

    void confirmEmail(String username, String token);

    void sendForgotPasswordEmail(String username);

    void sendForgotPasswordEmail(User user, TokenType tokenType);

    void resetPassword(String username, String token, VerificationPasswordResetRequest request);

    void sendEmailChangeConfirmation(User user, String newEmail);

    void confirmEmailChange(String username, String token);

    void sendPasswordChangedNotification(String email);
}
