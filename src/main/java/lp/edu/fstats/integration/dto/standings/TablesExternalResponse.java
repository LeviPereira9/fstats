package lp.edu.fstats.integration.dto.standings;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record TablesExternalResponse(
        String type,
        List<TableExternalResponse> table
) {

    public List<Long> getTeamExternalIds(){
        return table.stream().map(TableExternalResponse::getTeamExternalId).collect(Collectors.toList());
    }

    public Map<Long, TableExternalResponse> mapScoresByTeam(){
        return table.stream().collect(Collectors.toMap(
                TableExternalResponse::getTeamExternalId,
                Function.identity()
        ));
    }


}
