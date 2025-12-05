package lp.edu.fstats.dto.standings;

import lp.edu.fstats.model.standings.Standings;

import java.util.ArrayList;
import java.util.List;

public record StandingsResponse(
    List<TeamPositionResponse> standings
) {

    public static StandingsResponse toResponse(List<Standings> standings){
        List<TeamPositionResponse> teamPositionResponses = new ArrayList<>();

        for(Standings standing : standings){
            teamPositionResponses.add(new TeamPositionResponse(standing));
        }

        return new StandingsResponse(teamPositionResponses);
    }


}
