package lp.edu.fstats.integration.dto.standings;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record StandingsExternalResponse(
        List<TablesExternalResponse> standings
) {

    public TablesExternalResponse getFullTable(){
        return standings.stream().filter(
                s -> s.type().equals("TOTAL")
        ).findFirst().orElse(null);
    }

    public Map<String, TablesExternalResponse> getTables(){
        return standings.stream().collect(Collectors.toMap(
                TablesExternalResponse::type, Function.identity()
        ));
    }

}
