package lp.edu.fstats.config.restClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FootballRestClientConfig {
    @Value("${api.external.url}")
    private String apiUrl;
    @Value("${api.external.key}")
    private String apiKey;
    @Value("${api.external.value}")
    private String apiValue;

    @Bean
    public RestClient footballRestClient() {
        return RestClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader(apiKey,apiValue)
                .build();
    }
}
