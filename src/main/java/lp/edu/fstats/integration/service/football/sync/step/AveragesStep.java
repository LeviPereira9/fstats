package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.service.averages.AveragesService;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AveragesStep {

    private final AveragesService averagesService;

    public void sync(CompetitionSyncContext csc, TeamSyncContext tsc, StandingsSyncContext ssc){

        Competition competition = csc.getCompetition();

        Map<Long, Team> mapTeams = tsc.mappedTeamsByExternalId();

        Map<String, TablesExternalResponse> mapTables = ssc.getMapTables();

        Map<Long, Averages> currentAveragesMappedByTeamId = averagesService.findAllByCompetitionId(csc.getId());

        List<Averages> averagesToSave = new ArrayList<>();

        TablesExternalResponse homeTable = mapTables.get("HOME");
        TablesExternalResponse awayTable = mapTables.get("AWAY");

        Map<Long, TableExternalResponse> mapHomeTableByTeam = homeTable.mapScoresByTeam();
        Map<Long, TableExternalResponse> mapAwayTableByTeam = awayTable.mapScoresByTeam();

        for (Team team : mapTeams.values()) {

            Long externalTeamId = team.getExternalId();

            boolean hasHomeScore = mapHomeTableByTeam.containsKey(externalTeamId);
            boolean hasAwayScore = mapAwayTableByTeam.containsKey(externalTeamId);

            boolean teamAlreadyHasAverages = currentAveragesMappedByTeamId.containsKey(team.getId());

            if (!hasHomeScore && !hasAwayScore) {
                continue;
            }

            TableExternalResponse home = mapHomeTableByTeam.get(externalTeamId);
            TableExternalResponse away = mapAwayTableByTeam.get(externalTeamId);

            Averages averages = currentAveragesMappedByTeamId
                    .getOrDefault(team.getId(), new Averages());

            if(!teamAlreadyHasAverages){
                averages.setTeam(team);
                averages.setCompetition(competition);
            }

            if (home != null) {
                averages.setAvgGoalsForHome(
                        calculateAverage(home.goalsFor(), home.playedGames())
                );

                averages.setAvgGoalsAgainstHome(
                        calculateAverage(home.goalsAgainst(), home.playedGames())
                );
            }

            if (away != null) {
                averages.setAvgGoalsForAway(
                        calculateAverage(away.goalsFor(), away.playedGames())
                );

                averages.setAvgGoalsAgainstAway(
                        calculateAverage(away.goalsAgainst(), away.playedGames())
                );
            }

            averagesToSave.add(averages);
        }

        averagesService.saveAll(averagesToSave);
    }

    private BigDecimal calculateAverage(Integer goals, Integer games) {
        if (games == null || games == 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(goals)
                .divide(BigDecimal.valueOf(games), 2, RoundingMode.HALF_UP);
    }

}
