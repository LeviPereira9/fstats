package lp.edu.fstats.integration.client;

import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamsExternalResponse;
import lp.edu.fstats.integration.service.rateLimiter.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Year;

@Component
public class FootballApiClient {

    private final RestClient restClient;

    private final RateLimiter rateLimiter;

    public FootballApiClient(
            @Qualifier("footballRestClient") RestClient restClient,
            RateLimiter rateLimiter) {
        this.restClient = restClient;
        this.rateLimiter = rateLimiter;
    }

    @Retryable(
            retryFor = Exception.class,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public MatchesExternalResponse getCurrentMatches(String code, Year season, Integer matchday){
        rateLimiter.acquire();

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

    @Retryable(
            retryFor = Exception.class,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public CompetitionExternalResponse getCurrentCompetition(String code){
        rateLimiter.acquire();

        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+code)
                                .build())
                .retrieve()
                .body(CompetitionExternalResponse.class);
    }

    @Retryable(
            retryFor = Exception.class,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public StandingsExternalResponse getCurrentTotalStandings(String code, Year season){
        rateLimiter.acquire();

        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+code+"/standings")
                                .queryParam("season", season)
                                .build())
                .retrieve()
                .body(StandingsExternalResponse.class);
    }

    @Retryable(
            retryFor = Exception.class,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public CompetitionTeamsExternalResponse getCurrentTeams(String code, Year season){
        rateLimiter.acquire();

        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/competitions/"+ code +"/teams")
                                .queryParam("season", season)
                                .build())
                .retrieve()
                .body(CompetitionTeamsExternalResponse.class);
    }
}
