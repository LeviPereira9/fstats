package lp.edu.fstats.dto.match;

import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;

public record MatchResponse(
        //Long id,
        TeamResponse home,
        TeamResponse away,
        LocalDateTime date
) {

    public MatchResponse(Match match) {


        this(
                //match.getId(),
                new TeamResponse(
                        match.getHomeTeam().getId(),
                        match.getHomeTeam().getShortName(),
                        match.getHomeGoals(),
                        match.getHomeTeam().getCrest()),
                new TeamResponse(
                        match.getAwayTeam().getId(),
                        match.getAwayTeam().getShortName(),
                        match.getAwayGoals(),
                        match.getAwayTeam().getCrest()),
                match.getUtcDate()
        );
    }
}
