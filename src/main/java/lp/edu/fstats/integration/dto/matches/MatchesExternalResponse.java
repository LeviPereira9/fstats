package lp.edu.fstats.integration.dto.matches;

import lp.edu.fstats.integration.dto.matches.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.competition.ResultSetExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;
import lp.edu.fstats.model.competition.Competition;

import java.util.List;


public record MatchesExternalResponse (
        CompetitionExternalResponse competition,
        ResultSetExternalResponse resultSet,
        List<MatchExternalResponse> matches
){

    public List<Long> getMatchesExternalIds(){
        return matches
                .stream()
                .map(MatchExternalResponse::id)
                .toList();
    }

    public List<Long> getTeamsExternalIds(){
        return matches
                .stream()
                .flatMap(m-> m.getTeamExternalIds().stream())
                .toList();
    }

    public Long getCompetitionExternalId(){
        return competition.id();
    }

    public Competition competitionToModel(){
        Competition competitionModel = new Competition();

        competitionModel.setExternalId(competition.id());
        competitionModel.setName(competition.name());
        competitionModel.setCode(competition.code());
        competitionModel.setType(competition.type());
        competitionModel.setEmblem(competition.emblem());

        competitionModel.setCount(resultSet.count());
        competitionModel.setStartDate(resultSet.first());
        competitionModel.setEndDate(resultSet.last());

        return competitionModel;
    }

    public boolean allMatchesFinished(){
        return matches.stream().allMatch(m -> m.status().equals("FINISHED"));
    }

}
