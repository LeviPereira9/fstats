package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;

public class StandingsTestFactory {

    public static Standings buildStandings(
            Team team,
            Competition competition
    ){

        Standings standings = new Standings();
        standings.setId(1L);
        standings.setTeam(team);
        standings.setCompetition(competition);
        standings.setPosition(1);
        standings.setPoints(20);
        standings.setPlayedGames(8);

        return standings;
    }

}
