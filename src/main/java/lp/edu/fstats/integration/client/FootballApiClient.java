package lp.edu.fstats.integration.client;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FootballApiClient {

    private final RestClient restClient;

    public MatchesExternalResponse getCurrentMatches(String season, Integer matchday){
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/PL/matches")
                                .queryParam("season", season)
                                .queryParam("matchday", matchday)
                                .build())
                .retrieve()
                .body(MatchesExternalResponse.class);
    }

}
