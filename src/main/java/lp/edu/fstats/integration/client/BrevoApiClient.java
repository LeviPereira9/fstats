package lp.edu.fstats.integration.client;

import lp.edu.fstats.integration.dto.email.BrevoSendEmail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BrevoApiClient {

    private final RestClient restClient;

    public BrevoApiClient(@Qualifier("brevoRestClient") RestClient restClient){
        this.restClient = restClient;
    }

    public void sendEmail(BrevoSendEmail body){
        restClient.post()
                .uri(uriBuilder ->
                        uriBuilder.path("/v3/smtp/email").build())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

}
