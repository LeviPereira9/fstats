package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.service.standings.StandingsService;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StandingsSyncStep {

    private final FootballApiClient footballApiClient;
    private final StandingsRepository standingsRepository;

    private final StandingsService standingsService;

    public StandingsSyncContext sync(CompetitionSyncContext csc, TeamSyncContext tsc){
        StandingsSyncContext standingsSyncContext = new StandingsSyncContext();

        Competition competition = csc.getCompetition();

        StandingsExternalResponse externalStandings = footballApiClient.getCurrentTotalStandings(competition.getCode(), csc.getSeason());

        Map<Long, Standings> currentStandingsMappedByTeamId = standingsService.findAllByCompetitionId(competition.getId());

        Map<String, TablesExternalResponse> mapTables = externalStandings.getTables();
        standingsSyncContext.setMapTables(mapTables);

        TablesExternalResponse fullTable = mapTables.get("TOTAL");

        Map<Long, Team> mapTeams = tsc.mappedTeamsByExternalId();

        List<Standings> standingsToSave = new ArrayList<>();

        for(TableExternalResponse table: fullTable.table()){
            boolean alreadyExists = currentStandingsMappedByTeamId.containsKey(table.getTeamExternalId());
            Standings standings;

            if(alreadyExists){
                standings = currentStandingsMappedByTeamId.get(table.getTeamExternalId());
                table.update(standings);
            } else {
                standings = table.toModel(mapTeams.get(table.getTeamExternalId()), competition);
            }

            standingsToSave.add(standings);
        }

        standingsRepository.saveAll(standingsToSave);

        return standingsSyncContext;
    }

}
