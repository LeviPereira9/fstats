package lp.edu.fstats.integration.service.football.sync.context;

import lombok.Data;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;

import java.util.Map;

@Data
public class StandingsSyncContext {
    Map<String, TablesExternalResponse> mapTables;
}
