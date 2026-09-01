package lp.edu.fstats.service.email;

import lp.edu.fstats.integration.client.BrevoApiClient;
import lp.edu.fstats.integration.dto.email.BrevoSendEmail;
import lp.edu.fstats.integration.dto.email.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Profile({"prod", "uni"})
public class BrevoEmailService implements EmailService {

    private final BrevoApiClient brevoApiClient;
    @Value(("${brevo.name}"))
    private String name;

    @Value("${brevo.email}")
    private String email;

    public BrevoEmailService(BrevoApiClient brevoApiClient) {this.brevoApiClient = brevoApiClient;}

    private void sendEmail(String to, String subject, String content) {
        this.sendEmail(new String[]{to}, subject, content);
    }

    private void sendEmail(String[] to, String subject, String content){
        Email from = new Email(name, email);

        List<Email> recipients = Arrays.stream(to)
                .map(e -> new Email("Usuário", e))
                .toList();

        BrevoSendEmail brevoSendEmail = BrevoSendEmail.builder()
                .sender(from)
                .to(recipients)
                .subject(subject)
                .textContent(content)
                .build();

        brevoApiClient.sendEmail(brevoSendEmail);
    }

    @Async("emailThread")
    @Override
    public void sendConfirmationEmail(String to, String token) {
        String subject = "[Goal Radar]: Confirmação de e-mail.";
        String content = """
        Olá!
    
        Para confirmar seu endereço de e-mail, utilize o código de verificação abaixo na plataforma:
    
        %s
        
        Caso não tenha criado uma conta no Goal Radar, basta ignorar esta mensagem.
        """.formatted(token);

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendForgotPasswordEmail(String to, String token) {
        String subject = "[Goal Radar]: Redefinição de senha";
        String content = """
        Olá!
    
        Recebemos uma solicitação para redefinir a sua senha no Goal Radar.
        
        Utilize o código de segurança abaixo na tela do aplicativo para continuar o processo:
    
        %s
    
        Se você não solicitou essa alteração, nenhuma ação é necessária. Sua conta continua segura e você pode ignorar este e-mail.
        """.formatted(token);

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendPasswordChangedNotification(String to) {
        String subject = "[Goal Radar]: Aviso de segurança - Senha atualizada";
        String content = """
        Olá,
        
        A senha da sua conta no Goal Radar foi alterada recentemente.
        
        Se foi você quem realizou essa alteração, nenhuma ação adicional é necessária.
        
        Caso você não reconheça essa atividade, acesse o Goal Radar imediatamente e utilize a opção "Esqueci minha senha" para proteger sua conta, ou entre em contato com nosso suporte.
        """;

        this.sendEmail(to, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendEmailChangeConfirmation(String email, String token) {
        String subject = "[Goal Radar]: Confirmação para troca de e-mail";
        String content = """
        Olá!
    
        Recebemos uma solicitação para alterar o e-mail da sua conta no Goal Radar.
        Para confirmar a mudança, insira o código abaixo na aplicação:
    
        %s
    
        Se você não solicitou essa alteração, ignore este e-mail. Nenhuma mudança será feita na sua conta.
        """.formatted(token);

        this.sendEmail(email, subject, content);
    }

    @Async("emailThread")
    @Override
    public void sendEmailChangedNotification(String newEmail, String oldEmail) {
        String subject = "[Goal Radar]: Aviso de segurança - E-mail alterado";
        String content = """
        Olá!
    
        O endereço de e-mail associado à sua conta no Goal Radar foi alterado com sucesso.
    
        - Novo e-mail: %s
        - Antigo e-mail: %s
    
        Se você reconhece essa alteração, não é necessário fazer nada.
        
        Caso não tenha sido você, contate nosso suporte imediatamente.
        """.formatted(newEmail, oldEmail);

        this.sendEmail(new String[]{newEmail, oldEmail}, subject, content);
    }
}
