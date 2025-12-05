package lp.edu.fstats.dto.standings;

import jakarta.persistence.Column;
import lp.edu.fstats.model.standings.Standings;

public record TeamPositionResponse(
    Long teamId,
    String teamShortName,
    Integer position,
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
    public TeamPositionResponse (Standings source){
        this(
                source.getTeam().getId(),
                source.getTeam().getShortName(),
                source.getPosition(),
                source.getPlayedGames(),
                source.getForm(),
                source.getWon(),
                source.getDraw(),
                source.getLost(),
                source.getPoints(),
                source.getGoalsFor(),
                source.getGoalsAgainst(),
                source.getGoalDifference()
        );
    }
}