package lp.edu.fstats.steps;

import lp.edu.fstats.factory.apiResponse.FootballResponseFactory;
import lp.edu.fstats.factory.context.SyncContextTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.factory.entity.MatchTestFactory;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.*;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.MatchSyncStep;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.service.match.MatchService;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MatchSyncStepTest {

    @Mock
    private FootballApiClient footballApiClient;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchSyncStep matchSyncStep;

    //helpers

    private Competition buildCompetition(
            int lastCompletedMatchDay,
            int storedMatchDay,
            int apiCurrentMatchDay){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        competition.setLastCompletedMatchDay(lastCompletedMatchDay);
        competition.setStoredMatchDay(storedMatchDay);
        competition.setApiCurrentMatchDay(apiCurrentMatchDay);

        competition.setStatus("Em andamento");

        return competition;
    }



    // cenário sem partidas na rodada, condição de parada imediata.

    @Test
    void sync_shouldSaveCompetitionAndReturn_whenNoMatchesFound(){

        Competition competition = this.buildCompetition(0, 1, 1);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(1)))
                .thenReturn(FootballResponseFactory.buildExternalMatches(List.of()));

        matchSyncStep.sync(csc, tsc);

        verify(competitionService).saveCompetition(competition);
        verify(matchService, never()).saveAll(anyList());
    }

    // cenário sem partidas, competição termina
    @Test
    void sync_shouldMarkCompetitionAsFinished_whenNoMatchesAndCompetitionIsFinished(){

        Competition competition = this.buildCompetition(1, 1, 1);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(2)))
                .thenReturn(FootballResponseFactory.buildExternalMatches(List.of()));

        matchSyncStep.sync(csc, tsc);

        assertEquals("Finalizada", competition.getStatus());

        verify(competitionService).saveCompetition(competition);
    }

    // cenário sincroniza uma rodada e para duas a frente
    @Test
    void sync_shouldSaveMatchesAndStop_whenTwoMatchDaysAhead(){
        Competition competition = this.buildCompetition(0, 3, 1);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team home = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team away = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(home, away));

        MatchExternalResponse externalMatch = FootballResponseFactory.buildExternalMatch(500L, "SCHEDULED", 100L, 200L);
        MatchesExternalResponse matches = FootballResponseFactory.buildExternalMatches(List.of(externalMatch));

        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(1))).thenReturn(matches);

        when(matchService.findAllByExternalId(List.of(500L)))
                .thenReturn(Map.of());

        matchSyncStep.sync(csc, tsc);

        verify(matchService).saveAll(anyList());

        verify(competitionService).saveCompetition(competition);

        //so uma chamada a API, parou por estar duas rodadas a frente
        verify(footballApiClient, times(1)).getCurrentMatches(any(), any(), anyInt());
    }

    // cenário sincroniza duas rodadas, recursão controlada
    @Test
    void sync_shouldSyncTwoMatchDays_whenFirstMatchDayFinishedAndSecondIsEmpty(){
        Competition competition = this.buildCompetition(0, 1, 2);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team home = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team away = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(home, away));

        MatchExternalResponse finishedMatch = FootballResponseFactory.buildExternalMatch(500L, "FINISHED", 100L, 200L);
        MatchesExternalResponse firstMatchDay = FootballResponseFactory.buildExternalMatches(List.of(finishedMatch));

        // primeira tem rodadas finalizadas, segunda está vazia, ai para
        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(1)))
                .thenReturn(firstMatchDay);

        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(2)))
                .thenReturn(FootballResponseFactory.buildExternalMatches(List.of()));

        when(matchService.findAllByExternalId(anyList()))
                .thenReturn(Map.of());

        matchSyncStep.sync(csc, tsc);

        // duas chamadas
        verify(footballApiClient, times(2)).getCurrentMatches(any(), any(), anyInt());

        //Salvou só a primeira
        verify(matchService, times(1)).saveAll(anyList());
    }

    // cenário partida já existe no banco, ent atualiza
    @Test
    void sync_shouldUpdateExistingMatch_whenMatchAlreadyExistsInDatabase(){

        Competition competition = this.buildCompetition(0, 3, 1);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team home = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team away = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(home, away));

        MatchExternalResponse externalMatch = FootballResponseFactory.buildExternalMatch(500L, "FINISHED", 100L, 200L);
        MatchesExternalResponse matches = FootballResponseFactory.buildExternalMatches(List.of(externalMatch));

        Match existingMatch = MatchTestFactory.buildMatch(10L, 500L, home, away, 1, competition);

        when(footballApiClient.getCurrentMatches(eq("PL"), any(Year.class), eq(1)))
                .thenReturn(matches);

        when(matchService.findAllByExternalId(List.of(500L)))
                .thenReturn(Map.of(500L, existingMatch));

        matchSyncStep.sync(csc, tsc);

        ArgumentCaptor<List<Match>> captor = ArgumentCaptor.forClass(List.class);

        verify(matchService).saveAll(captor.capture());

        Match savedMatch = captor.getValue().get(0);

        assertEquals(10L, savedMatch.getId());
        assertEquals("FINISHED", savedMatch.getStatus());

    }
}
