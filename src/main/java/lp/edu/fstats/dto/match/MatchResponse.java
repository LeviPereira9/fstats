package lp.edu.fstats.dto.match;

import lp.edu.fstats.model.match.Match;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        String homeTeam,
        String awayTeam,
        LocalDateTime date
) {

    public MatchResponse(Match match) {
        this(
                match.getId(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getUtcDate()
        );
    }
}
