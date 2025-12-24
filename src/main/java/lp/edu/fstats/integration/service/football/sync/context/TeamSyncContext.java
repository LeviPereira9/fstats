package lp.edu.fstats.integration.service.football.sync.context;

import lombok.Data;
import lp.edu.fstats.model.team.Team;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TeamSyncContext {
    private List<Team> teams;

    public Map<Long, Team> mappedTeamsByExternalId() {
        return teams.stream().collect(Collectors.toMap(
                Team::getExternalId, Function.identity()
        ));
    }
}
