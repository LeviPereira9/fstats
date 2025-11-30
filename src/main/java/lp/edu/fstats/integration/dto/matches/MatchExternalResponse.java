package lp.edu.fstats.integration.dto.matches;

import com.fasterxml.jackson.annotation.JsonProperty;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MatchExternalResponse(
        Integer id,
        LocalDateTime utcDate,
        String status,
        @JsonProperty("matchday")
        Integer matchDay,
        String stage,
        TeamExternalResponse homeTeam,
        TeamExternalResponse awayTeam,
        ScoreExternalResponse score
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

    public Map<Integer, TeamExternalResponse> getTeams(){
        Map<Integer, TeamExternalResponse> teams = new HashMap<>();
        teams.put(homeTeam.id(), homeTeam);
        teams.put(awayTeam.id(), awayTeam);

        return teams;
    }

    public List<Integer> getTeamExternalIds(){
        return List.of(homeTeam.id(), awayTeam.id());
    }

    public Integer getHomeTeamExternalId(){
        return homeTeam.id();
    }

    public Integer getAwayTeamExternalId(){
        return awayTeam.id();
    }

    public Integer getScoreExternalId(){
        return id();
    }

    public Match toModel(){
        Match match = new Match();

        match.setExternalId(id);
        match.setUtcDate(utcDate);
        match.setStatus(status);
        match.setMatchDay(matchDay);
        match.setStage(stage);
        match.setWinner(score.winner());
        match.setHomeGoals(score.fullTime().home());
        match.setAwayGoals(score.fullTime().away());

        return match;
    }

    public Team homeTeamToModel(){
        return homeTeam.toModel();
    }

    public Team awayTeamToModel(){
        return awayTeam.toModel();
    }



}
