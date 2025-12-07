package lp.edu.fstats.dto.match;

import lp.edu.fstats.dto.probability.ProbabilityResponse;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;

public record MatchResponse(
        //Long id,
        TeamResponse home,
        TeamResponse away,
        ProbabilityResponse probability,
        LocalDateTime date
) {

    public MatchResponse(Match source) {

        this(
                //match.getId(),
                new TeamResponse(
                        source.getHomeTeam(),
                        source.getHomeGoals(),
                        source.getHomeTeam().getCrest()),
                new TeamResponse(
                        source.getAwayTeam(),
                        source.getAwayGoals(),
                        source.getAwayTeam().getCrest()),
                (source.getProbability() == null ? null :
                        new ProbabilityResponse(source.getProbability())),
                source.getUtcDate()
        );
    }
}
