package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;

import java.math.BigDecimal;

public class AveragesTestFactory {

    public static Averages buildAverages(Team team, Competition competition){
        Averages averages = new Averages();
        averages.setId(1L);
        averages.setTeam(team);
        averages.setCompetition(competition);

        averages.setAvgGoalsForHome(BigDecimal.valueOf(1.5));
        averages.setAvgGoalsAgainstHome(BigDecimal.valueOf(0.8));
        averages.setAvgGoalsForAway(BigDecimal.valueOf(1.1));
        averages.setAvgGoalsAgainstAway(BigDecimal.valueOf(1.3));

        return averages;
    }

}
