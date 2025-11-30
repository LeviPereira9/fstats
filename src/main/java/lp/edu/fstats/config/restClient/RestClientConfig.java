package lp.edu.fstats.config.restClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${api.external.url}")
    private String apiUrl;
    @Value("${api.external.key}")
    private String apiKey;
    @Value("${api.external.value}")
    private String apiValue;

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader(apiKey,apiValue)
                .build();
    }
}
