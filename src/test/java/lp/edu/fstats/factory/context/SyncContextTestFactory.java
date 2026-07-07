package lp.edu.fstats.factory.context;

import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncContextTestFactory {

    public static CompetitionSyncContext buildCsc(Competition competition){
        CompetitionSyncContext csc = new CompetitionSyncContext();
        csc.setCompetition(competition);

        return csc;
    }

    public static TeamSyncContext buildTsc(List<Team> teams){
        TeamSyncContext tsc = new TeamSyncContext();
        tsc.setTeams(teams);

        return tsc;
    }

    public static StandingsSyncContext buildSsc(
            List<TableExternalResponse> homeTable,
            List<TableExternalResponse> awayTable){

        TablesExternalResponse home = new TablesExternalResponse("HOME", homeTable);
        TablesExternalResponse away = new TablesExternalResponse("AWAY", awayTable);
        TablesExternalResponse total = new TablesExternalResponse("TOTAL", new ArrayList<>());

        Map<String, TablesExternalResponse> mapTables = Map.of(
                "HOME", home,
                "AWAY", away,
                "TOTAL", total
        );

        StandingsSyncContext ssc = new StandingsSyncContext();

        ssc.setMapTables(mapTables);

        return ssc;
    }

}
