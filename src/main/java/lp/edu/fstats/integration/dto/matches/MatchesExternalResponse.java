package lp.edu.fstats.integration.dto.matches;

import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;

import java.util.List;


public record MatchesExternalResponse (
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

    public boolean allMatchesFinished(){
        return matches.stream().allMatch(m -> m.status().equals("FINISHED"));
    }

}
