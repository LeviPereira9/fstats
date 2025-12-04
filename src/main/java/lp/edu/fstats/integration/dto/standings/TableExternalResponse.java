package lp.edu.fstats.integration.dto.standings;

import lp.edu.fstats.integration.dto.matches.match.TeamExternalResponse;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;

public record TableExternalResponse(
        Integer position,
        TeamExternalResponse team,
        Integer playedGames,
        String form,
        Integer won,
        Integer draw,
        Integer lost,
        Integer points,
        Integer goalsFor,
        Integer goalsAgainst,
        Integer goalDifference
) {

    public Long getTeamExternalId(){
        return team.id();
    }

    public Standings toModel(Team team, Competition competition){
        Standings standings = new Standings();

        standings.setTeam(team);
        standings.setCompetition(competition);

        this.map(standings);

        return standings;
    }

    public void update(Standings target){
        this.map(target);
    }

    private void map(Standings target){
        target.setPosition(position);
        target.setPlayedGames(playedGames);
        target.setForm(form);
        target.setWon(won);
        target.setDraw(draw);
        target.setLost(lost);
        target.setPoints(points);
        target.setGoalsFor(goalsFor);
        target.setGoalsAgainst(goalsAgainst);
        target.setGoalDifference(goalDifference);
    }

}
