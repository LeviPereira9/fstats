package lp.edu.fstats.integration.dto.matches.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MatchExternalResponse(
        Long id,
        LocalDateTime utcDate,
        String status,
        @JsonProperty("matchday")
        Integer matchDay,
        String stage,
        TeamExternalResponse homeTeam,
        TeamExternalResponse awayTeam,
        ScoreExternalResponse score,
        SeasonExternalResponse season
) {

    //Oq eu quero? Passar os external ID e verificar se já existem.
    // Quem já existe eu ignoro, quem n existe eu busco.
    // Como?
    // external = (1,2)
    // existing = check (external)
    // existing = (1)
    // notExisting = external.removeAll(existing);
    // notExisting = (2)
    // Melhor coisa que eu posso fazer:
    // Mapzudo de <Id, Time>;

    public List<Long> getTeamExternalIds(){
        return List.of(homeTeam.id(), awayTeam.id());
    }

    public Long getHomeTeamExternalId(){
        return homeTeam.id();
    }

    public Long getAwayTeamExternalId(){
        return awayTeam.id();
    }

    public Match toModel(){
        Match match = new Match();

        match.setExternalId(id);

        this.map(match);

        return match;
    }

    public Match update(Match target){

        this.map(target);

        return target;
    }

    private void map(Match target){
        target.setUtcDate(utcDate);
        target.setStatus(status);
        target.setMatchDay(matchDay);
        target.setStage(stage);
        target.setWinner(score.winner());
        target.setHomeGoals(score.fullTime().home());
        target.setAwayGoals(score.fullTime().away());
    }



}
