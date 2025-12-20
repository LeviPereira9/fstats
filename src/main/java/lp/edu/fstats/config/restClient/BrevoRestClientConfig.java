package lp.edu.fstats.config.restClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BrevoRestClientConfig {
    @Value("${brevo.url}")
    private String url;
    @Value("${brevo.header}")
    private String header;
    @Value("${brevo.key}")
    private String key;

    @Bean
    public RestClient brevoRestClient() {
        return RestClient
                .builder()
                .baseUrl(url)
                .defaultHeaders(headers -> {
                    headers.set(header, key);
                    headers.set("content-type", "application/json");
                    headers.set("accept", "application/json");
                })
                .build();
    }
}
