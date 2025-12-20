package lp.edu.fstats.integration.dto.email;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BrevoSendEmail {
    private Email sender;
    private List<Email> to;
    private String subject;
    private String textContent;
}
