package lp.edu.fstats.integration.dto.teams;

import java.util.List;

public record CompetitionTeamsExternalResponse(
        List<CompetitionTeamExternalResponse> teams
) {

    public List<Long> getExternalIds() {
        return teams.stream().map(CompetitionTeamExternalResponse::id).toList();
    }

}
