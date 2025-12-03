package lp.edu.fstats.integration.client;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FootballApiClient {

    private final RestClient restClient;

    public MatchesExternalResponse getCurrentMatches(String code, String season, Integer matchday){
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+code+"/matches")
                                .queryParam("season", season)
                                .queryParam("matchday", matchday)
                                .build())
                .retrieve()
                .body(MatchesExternalResponse.class);
    }

    public CompetitionExternalResponse getCurrentCompetition(String code){
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+code)
                                .build())
                .retrieve()
                .body(CompetitionExternalResponse.class);
    }

}
