/*
package lp.edu.fstats.service.email;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.exception.custom.CustomBadGatewayException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private void sendEmail(String to, String subject, String content) {
        this.sendEmail(new String[]{to}, subject, content);
    }

    private void sendEmail(String[] to, String subject, String content) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
        } catch (Exception e) {
            throw CustomBadGatewayException.email();
        }
    }

    @Async("emailThread")
    @Override
    public void sendConfirmationEmail(String to, String token) {
        String subject = "[Pode Apostar]: Confirmação de e-mail.";
        String content = """
        Olá!

        Para confirmar seu endereço de e-mail, utilize o código abaixo:

        %s
        
        Caso não tenha criado uma conta no Pode Apostar, basta ignorar esta mensagem.
        """.formatted(token);

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendForgotPasswordEmail(String to, String token) {
        String subject = "[Pode Apostar]: Redefinição de senha.";
        String content = """
        Olá!

        Recebemos uma solicitação para redefinir sua senha.
        
        Utilize o código abaixo para continuar o processo:

        %s

        Caso não tenha solicitado essa alteração, ignore este e-mail.
        """.formatted(token);

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendPasswordChangedNotification(String to) {
        String subject = "[Pode Apostar]: Senha atualizada.";
        String content = """
                        Olá,
                        Sua senha foi alterada recentemente.
                        Se você reconhece essa alteração, nenhuma ação é necessária.
                        
                        Caso não tenha sido você, redefina sua senha imediatamente pelo Link abaixo:
                        [link cavernoso]
                        """;

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendEmailChangeConfirmation(String email, String token) {
        String subject = "[Pode Apostar]: Troca de e-mail.";
        String content = """
        Olá!

        Recebemos uma solicitação para alterar o e-mail da sua conta.
        Para confirmar a mudança, utilize o código abaixo:

        %s

        Se você não solicitou essa alteração, ignore este e-mail.
        """.formatted(token);

        this.sendEmail(email, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendEmailChangedNotification(String newEmail, String oldEmail) {
        String subject = "[Pode Apostar]: Seu e-mail foi alterado.";
        String content = """
        Olá!

        O endereço de e-mail associado à sua conta Pode Apostar foi alterado com sucesso.

        - Novo e-mail: %s
        - Antigo e-mail: %s

        Se você reconhece essa alteração, nenhuma ação é necessária.
        Caso não tenha sido você, entre em contato com o suporte imediatamente.
        """.formatted(newEmail, oldEmail);

        this.sendEmail(new String[]{newEmail, oldEmail}, subject, content);
    }
}
*/
