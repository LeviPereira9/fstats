package lp.edu.fstats.integration.dto.matches;

import java.util.List;


public record MatchesExternalResponse (
        List<MatchExternalResponse> matches
){

    public List<Integer> getMatchesExternalIds(){
        return matches
                .stream()
                .map(MatchExternalResponse::id)
                .toList();
    }

    public List<Integer> getTeamsExternalIds(){
        return matches
                .stream()
                .flatMap(m-> m.getTeamExternalIds().stream())
                .toList();
    }

}
