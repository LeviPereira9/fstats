package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;

public class MatchTestFactory {

    public static Match buildMatch(Long id, Long externalId, Team home, Team away, Integer matchDay, Competition competition){
        Match match = new Match();
        match.setId(id);
        match.setExternalId(externalId);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeGoals(2);
        match.setAwayGoals(1);
        match.setStatus("FINISHED");
        match.setMatchDay(matchDay);
        match.setUtcDate(LocalDateTime.of(2024, 5, 1, 16, 0));

        return match;
    }

}
