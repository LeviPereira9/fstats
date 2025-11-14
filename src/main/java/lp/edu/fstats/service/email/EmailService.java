package lp.edu.fstats.service.email;

public interface EmailService {
    void sendConfirmationEmail(String to, String token);

    void sendForgotPasswordEmail(String to, String token);

    void sendPasswordChangedNotification(String to);

    void sendEmailChangeConfirmation(String email, String token);

    void sendEmailChangedNotification(String newEmail, String oldEmail);
}
