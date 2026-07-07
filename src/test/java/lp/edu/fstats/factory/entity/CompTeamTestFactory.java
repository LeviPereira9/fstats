package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.competition.CompetitionTeam;
import lp.edu.fstats.model.team.Team;

public class CompTeamTestFactory {

    public static CompetitionTeam buildCompetitionTeam(Team team, Competition competition){
        CompetitionTeam ct = new CompetitionTeam();

        ct.setId(1L);
        ct.setTeam(team);
        ct.setCompetition(competition);

        return ct;
    }

}
