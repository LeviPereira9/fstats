package lp.edu.fstats.service;

import lp.edu.fstats.exception.custom.CustomBadGatewayException;
import lp.edu.fstats.service.email.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    //sendConfirmationEmail
    @Test
    void sendConfirmationEmail_shouldSendEmail_withCorrectRecipientAndSubject() {

        emailService.sendConfirmationEmail("joao@email.com", "123456");


        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertArrayEquals(new String[]{"joao@email.com"}, sentMessage.getTo());
        assertEquals("[Pode Apostar]: Confirmação de e-mail.", sentMessage.getSubject());

        assertTrue(sentMessage.getText().contains("123456"));

    }

    //sendForgotPasswordEmail
    @Test
    void sendForgotPasswordEmail_shouldSendEmail_withCorrectRecipientAndToken(){
        emailService.sendForgotPasswordEmail("joao@email.com", "abcdef");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertArrayEquals(new String[]{"joao@email.com"}, sentMessage.getTo());
        assertEquals("[Pode Apostar]: Redefinição de senha.", sentMessage.getSubject());

        assertTrue(sentMessage.getText().contains("abcdef"));
    }

    // sendPasswordChangedNotification
    @Test
    void sendPasswordChangedNotification_shouldSendEmail_withCorrectRecipient(){
        emailService.sendPasswordChangedNotification("joao@email.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertArrayEquals(new String[]{"joao@email.com"}, sentMessage.getTo());
        assertEquals("[Pode Apostar]: Senha atualizada.", sentMessage.getSubject());

    }

    //sendEmailChangeConfirmation
    @Test
    void sendEmailChangeConfirmation_shouldSendEmail_withCorrectRecipientAndToken(){
        emailService.sendEmailChangeConfirmation("joao@email.com", "token123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertArrayEquals(new String[]{"joao@email.com"}, sentMessage.getTo());
        assertEquals("[Pode Apostar]: Troca de e-mail.", sentMessage.getSubject());

        assertTrue(sentMessage.getText().contains("token123"));

    }

    //sendEmailChangedNotification
    @Test
    void sendEmailChangedNotification_shouldSendEmail_toBothOldAndNewAddresses(){
        emailService.sendEmailChangedNotification("novo@email.com", "antigo@email.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertArrayEquals(new String[]{"novo@email.com", "antigo@email.com"}, sentMessage.getTo());
        assertEquals("[Pode Apostar]: Seu e-mail foi alterado.", sentMessage.getSubject());

        assertTrue(sentMessage.getText().contains("novo@email.com"));
        assertTrue(sentMessage.getText().contains("antigo@email.com"));

    }

    // tratamento de erro

    @Test
    void sendConfirmationEmail_shouldThrowBadGateway_whenMailSenderFails(){

        doThrow(new RuntimeException("SMTP indisponível")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(CustomBadGatewayException.class,
                ()-> emailService.sendConfirmationEmail("joao@email.com", "123456"));

    }
}
