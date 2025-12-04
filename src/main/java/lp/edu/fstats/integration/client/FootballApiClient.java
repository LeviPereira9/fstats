package lp.edu.fstats.integration.client;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class FootballApiClient {

    private final RestClient restClient;

    public MatchesExternalResponse getCurrentMatches(String code, Year season, Integer matchday){
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

    public StandingsExternalResponse getCurrentTotalStandings(String code, Year season){
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+code+"/standings")
                                .queryParam("season", season)
                                .build())
                .retrieve()
                .body(StandingsExternalResponse.class);
    }
}
