package lp.edu.fstats.steps;

import lp.edu.fstats.factory.apiResponse.FootballResponseFactory;
import lp.edu.fstats.factory.context.SyncContextTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.factory.entity.StandingsTestFactory;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.StandingsSyncStep;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.service.standings.StandingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StandingsSyncStepTest {

    @Mock
    private FootballApiClient footballApiClient;

    @Mock
    private StandingsRepository standingsRepository;

    @Mock
    private StandingsService standingsService;

    @InjectMocks
    private StandingsSyncStep standingsSyncStep;

    //helpers
    private TableExternalResponse buildTableEntry(Long externalId, String name){
        return FootballResponseFactory.buildTableEntry(
                externalId,
                name,
                1,
                10,
                5,
                5,
                10
        );

    }

    // cenário nenhum standings salvo, cria tod0s
    @Test
    void sync_shouldCreateNewStandings_whenNoStandingsExistForCompetition(){

        Competition competition = CompetitionTestFactory
                .buildCompetition("PL");

        Team arsenal = TeamTestFactory
                .buildTeam(1L, 100L, "Arsenal");

        Team chelsea = TeamTestFactory
                .buildTeam(2L, 200L, "Chelsea");

        CompetitionSyncContext csc = SyncContextTestFactory
                .buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory
                .buildTsc(List.of(arsenal, chelsea));

        TableExternalResponse arsenalTable = this
                .buildTableEntry(100L, "Arsenal");

        TableExternalResponse chelseaTable = this
                .buildTableEntry(200L, "Chelsea");

        StandingsExternalResponse externalStandings = FootballResponseFactory
                .buildExternalStandings(List.of(arsenalTable, chelseaTable));

        when(footballApiClient.getCurrentTotalStandings(eq("PL"), any(Year.class)))
                .thenReturn(externalStandings);

        when(standingsService.findAllByCompetitionId(1L))
                .thenReturn(Map.of());

        StandingsSyncContext result = standingsSyncStep.sync(csc, tsc);

        assertNotNull(result);
        assertNotNull(result.getMapTables());

        assertTrue(result.getMapTables().containsKey("TOTAL"));

        ArgumentCaptor<List<Standings>> captor = ArgumentCaptor.forClass(List.class);

        verify(standingsRepository).saveAll(captor.capture());

        List<Standings> saved = captor.getValue();
        assertEquals(2, saved.size());

        // confirma que os sntadings criados tem time competição corretas

        assertTrue(saved.stream().anyMatch(s -> s.getTeam().equals(arsenal)));
        assertTrue(saved.stream().anyMatch(s -> s.getTeam().equals(chelsea)));
        assertTrue(saved.stream().allMatch(s -> s.getCompetition().equals(competition)));
    }

    // cenário standings já existem, atualiza.
    @Test
    void sync_shouldUpdateExistingStandings_whenStandingsAlreadyExistForCompetition(){

        Competition competition = CompetitionTestFactory
                .buildCompetition("PL");

        Team arsenal = TeamTestFactory
                .buildTeam(1L, 100L, "Arsenal");

        CompetitionSyncContext csc = SyncContextTestFactory
                .buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory
                .buildTsc(List.of(arsenal));

        TableExternalResponse arsenalTable = this
                .buildTableEntry(100L, "Arsenal");

        StandingsExternalResponse externalStandings = FootballResponseFactory
                .buildExternalStandings(List.of(arsenalTable));

        Standings existingStandings = StandingsTestFactory
                .buildStandings(arsenal, competition);

        when(footballApiClient.getCurrentTotalStandings(eq("PL"), any(Year.class)))
                .thenReturn(externalStandings);

        // standings já existe, keyed pelo id interno do team
        when(standingsService.findAllByCompetitionId(1L))
                .thenReturn(Map.of(100L, existingStandings));

        standingsSyncStep.sync(csc, tsc);

        ArgumentCaptor<List<Standings>> captor = ArgumentCaptor.forClass(List.class);

        verify(standingsRepository).saveAll(captor.capture());

        List<Standings> saved = captor.getValue();
        assertEquals(1, saved.size());

        Standings updated = saved.get(0);

        //confirma que é o mesmo objeto, atualizado e n criado
        assertEquals(existingStandings.getId(), updated.getId());

        assertEquals(1, updated.getPosition());
        assertEquals(10, updated.getPoints());

    }

    // cenário misto, alguns existem, outros são novos.
    @Test
    void sync_shouldCreateAndUpdateStandings_whenSomeExistAndSomeAreNew(){

        Competition competition = CompetitionTestFactory
                .buildCompetition("PL");

        Team arsenal = TeamTestFactory
                .buildTeam(1L, 100L, "Arsenal");

        Team chelsea = TeamTestFactory
                .buildTeam(2L, 200L, "Chelsea");

        CompetitionSyncContext csc = SyncContextTestFactory
                .buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory
                .buildTsc(List.of(arsenal, chelsea));

        TableExternalResponse arsenalTable = this
                .buildTableEntry(100L, "Arsenal");

        TableExternalResponse chelseaTable = this
                .buildTableEntry(200L, "Chelsea");

        StandingsExternalResponse externalStandings = FootballResponseFactory
                .buildExternalStandings(List.of(arsenalTable, chelseaTable));

        Standings existingArsenalStandings = StandingsTestFactory
                .buildStandings(arsenal, competition);

        when(footballApiClient.getCurrentTotalStandings(eq("PL"), any(Year.class)))
                .thenReturn(externalStandings);

        when(standingsService.findAllByCompetitionId(1L))
                .thenReturn(Map.of(100L, existingArsenalStandings));

        standingsSyncStep.sync(csc, tsc);

        ArgumentCaptor<List<Standings>> captor = ArgumentCaptor.forClass(List.class);

        verify(standingsRepository).saveAll(captor.capture());

        List<Standings> saved = captor.getValue();

        assertEquals(2, saved.size());

        //Arsenal foi atualizado, tem id.
        assertTrue(saved.stream().anyMatch(s -> s.getId() != null && s.getTeam().equals(arsenal)));

        //Chelsea foi criado, n tem id
        assertTrue(saved.stream().anyMatch(s -> s.getId() == null && s.getTeam().equals(chelsea)));
    }
}
