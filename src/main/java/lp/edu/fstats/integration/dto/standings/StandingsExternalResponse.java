package lp.edu.fstats.integration.dto.standings;

import java.util.List;

public record StandingsExternalResponse(
        List<TablesExternalResponse> standings
) {

    public TablesExternalResponse getFullTable(){
        return standings.stream().filter(
                s -> s.type().equals("TOTAL")
        ).findFirst().orElse(null);
    }

}
