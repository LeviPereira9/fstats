package lp.edu.fstats.integration.service.football.sync.context;

import lombok.Data;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.Year;
import java.util.List;

@Data
public class CompetitionSyncContext {

    private Competition competition;
    private List<Team> teams;
    private List<Match> matches;

    public Year getSeason(){
        return Year.of(competition.getStartDate().getYear());
    }

    public boolean hasActiveCompetition(){
        return competition != null;
    }

    public Long getId(){
        return competition.getId();
    }

    public String getCode(){
        return competition.getCode();
    }

}
