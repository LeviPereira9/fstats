package lp.edu.fstats.service;

import lp.edu.fstats.integration.client.BrevoApiClient;
import lp.edu.fstats.integration.dto.email.BrevoSendEmail;
import lp.edu.fstats.service.email.BrevoEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrevoEmailServiceImplTest {

    @Mock
    private BrevoApiClient brevoApiClient;

    @InjectMocks
    private BrevoEmailService brevoEmailService;

    @BeforeEach
    void setup(){
        //@Value n é processado no Mockito, ent faz manualmente

        ReflectionTestUtils.setField(brevoEmailService, "name", "Pode Apostar");
        ReflectionTestUtils.setField(brevoEmailService, "email", "contato@podeapostar.com");
    }

    //sendConfirmationEmail
    @Test
    void sendConfirmationEmail_shouldSendEmail_withCorrectRecipientAndSubject(){

        brevoEmailService.sendConfirmationEmail("joao@email.com", "123456");

        ArgumentCaptor<BrevoSendEmail> captor = ArgumentCaptor.forClass(BrevoSendEmail.class);

        verify(brevoApiClient).sendEmail(captor.capture());

        BrevoSendEmail sentMessage = captor.getValue();

        assertEquals("[Pode Apostar]: Confirmação de e-mail.", sentMessage.getSubject());
        assertEquals("joao@email.com", sentMessage.getTo().get(0).getEmail());
        assertEquals(1, sentMessage.getTo().size());
        assertEquals("contato@podeapostar.com", sentMessage.getSender().getEmail());
        assertEquals("Pode Apostar", sentMessage.getSender().getName());

        assertTrue(sentMessage.getTextContent().contains("123456"));

    }

    // sendForgotPasswordEmail

    @Test
    void sendForgotPasswordEmail_shouldSendEmail_withCorrectRecipientAndToken(){
        brevoEmailService.sendForgotPasswordEmail("joao@email.com", "abcdef");

        ArgumentCaptor<BrevoSendEmail> captor = ArgumentCaptor.forClass(BrevoSendEmail.class);

        verify(brevoApiClient).sendEmail(captor.capture());

        BrevoSendEmail sentMessage = captor.getValue();

        assertEquals("[Pode Apostar]: Redefinição de senha.", sentMessage.getSubject());
        assertEquals("joao@email.com", sentMessage.getTo().get(0).getEmail());
        assertEquals(1, sentMessage.getTo().size());

        assertTrue(sentMessage.getTextContent().contains("abcdef"));
    }

    //sendPasswordChangedNotification
    @Test
    void sendPasswordChangedNotification_shouldSendEmail_withCorrectRecipient(){
        brevoEmailService.sendPasswordChangedNotification("joao@email.com");

        ArgumentCaptor<BrevoSendEmail> captor = ArgumentCaptor.forClass(BrevoSendEmail.class);

        verify(brevoApiClient).sendEmail(captor.capture());

        BrevoSendEmail sentMessage = captor.getValue();

        assertEquals("[Pode Apostar]: Senha atualizada.", sentMessage.getSubject());
        assertEquals("joao@email.com", sentMessage.getTo().get(0).getEmail());
        assertEquals(1, sentMessage.getTo().size());
    }

    // sendEmailChangeConfirmation
    @Test
    void sendEmailChangeConfirmation_shouldSendEmail_withCorrectRecipientAndToken(){
        brevoEmailService.sendEmailChangeConfirmation("joao@email.com", "token123");

        ArgumentCaptor<BrevoSendEmail> captor = ArgumentCaptor.forClass(BrevoSendEmail.class);

        verify(brevoApiClient).sendEmail(captor.capture());

        BrevoSendEmail sentMessage = captor.getValue();

        assertEquals("[Pode Apostar]: Troca de e-mail.", sentMessage.getSubject());
        assertEquals("joao@email.com", sentMessage.getTo().get(0).getEmail());
        assertEquals(1, sentMessage.getTo().size());

        assertTrue(sentMessage.getTextContent().contains("token123"));
    }

    //sendEmailChangedNotification
    @Test
    void sendEmailChangedNotification_shouldSendEmail_toBotOldAndNewAddresses(){
        brevoEmailService.sendEmailChangedNotification("novo@email.com", "antigo@email.com");

        ArgumentCaptor<BrevoSendEmail> captor = ArgumentCaptor.forClass(BrevoSendEmail.class);

        verify(brevoApiClient).sendEmail(captor.capture());

        BrevoSendEmail sentMessage = captor.getValue();

        assertEquals("[Pode Apostar]: Seu e-mail foi alterado.", sentMessage.getSubject());
        assertEquals(2, sentMessage.getTo().size());

        assertTrue(sentMessage.getTextContent().contains("novo@email.com"));
        assertTrue(sentMessage.getTextContent().contains("antigo@email.com"));
    }
}
