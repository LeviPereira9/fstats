package lp.edu.fstats.steps;

import lp.edu.fstats.factory.apiResponse.FootballResponseFactory;
import lp.edu.fstats.factory.context.SyncContextTestFactory;
import lp.edu.fstats.factory.entity.AveragesTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.AveragesStep;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.service.averages.AveragesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AveragesSyncStepTest {

    @Mock
    private AveragesService averagesService;

    @InjectMocks
    private AveragesStep averagesStep;

    //helpers
    private TableExternalResponse buildTableEntry(
            Long teamExternalId,
            String name,
            Integer goalsFor,
            Integer goalsAgainst,
            Integer playedGames
    ){
        return FootballResponseFactory.buildTableEntry(
                teamExternalId,
                name,
                1,
                20,
                goalsFor,
                goalsAgainst,
                playedGames
        );
    };

    // cenário time novo com scores home e away
    @Test
    void sync_shouldCreateNewAverages_whenTeamHasNoExistingAverages(){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(arsenal));

        TableExternalResponse homeEntry = this.buildTableEntry(100L, "Arsenal", 10, 4, 5);
        TableExternalResponse awayEntry = this.buildTableEntry(100L, "Arsenal", 6, 5, 5);

        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(homeEntry), List.of(awayEntry));

        when(averagesService.findAllByCompetitionId(1L)).thenReturn(Map.of());

        averagesStep.sync(csc, tsc, ssc);

        ArgumentCaptor<List<Averages>> captor = ArgumentCaptor.forClass(List.class);

        verify(averagesService).saveAll(captor.capture());

        List<Averages> saved = captor.getValue();

        assertEquals(1, saved.size());

        Averages averages = saved.get(0);

        assertEquals(arsenal, averages.getTeam());
        assertEquals(competition, averages.getCompetition());

        // 10 gols / 5 jogos = 2.00
        assertEquals(BigDecimal.valueOf(2.00).setScale(2), averages.getAvgGoalsForHome());
        // 4 gols sofridos / 5 jogos = 0.80
        assertEquals(BigDecimal.valueOf(0.80).setScale(2), averages.getAvgGoalsAgainstHome());
        // 6 gols / 5 jogos = 1.20
        assertEquals(BigDecimal.valueOf(1.20).setScale(2), averages.getAvgGoalsForAway());
        // 5 gols sofridos / 5 jogos = 1.00
        assertEquals(BigDecimal.valueOf(1.00).setScale(2), averages.getAvgGoalsAgainstAway());
    }

    // cenário: time com averages existentes, atualiza

    @Test
    void sync_shouldUpdateExistingAverages_whenTeamAlreadyHasAverages(){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(arsenal));

        TableExternalResponse homeEntry = this.buildTableEntry(100L, "Arsenal", 10, 4, 5);
        TableExternalResponse awayEntry = this.buildTableEntry(100L, "Arsenal", 6, 5, 5);

        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(homeEntry), List.of(awayEntry));

        Averages existingAverages = AveragesTestFactory.buildAverages(arsenal, competition);

        when(averagesService.findAllByCompetitionId(1L)).thenReturn(Map.of(1L, existingAverages));

        averagesStep.sync(csc, tsc, ssc);

        ArgumentCaptor<List<Averages>> captor = ArgumentCaptor.forClass(List.class);

        verify(averagesService).saveAll(captor.capture());

        List<Averages> saved = captor.getValue();
        assertEquals(1, saved.size());

        Averages updated = saved.get(0);

        //confirma que é o mesmo objeto.

        assertEquals(existingAverages.getId(), updated.getId());

        // 20 gols / 10 jogos = 2.00
        assertEquals(BigDecimal.valueOf(2.00).setScale(2), updated.getAvgGoalsForHome());
        // 12 gols / 10 jogos = 1.20
        assertEquals(BigDecimal.valueOf(1.20).setScale(2), updated.getAvgGoalsForAway());
    }

    // cenário: time sem scores home e away, é pulado

    @Test
    void  sync_shouldSkipTeam_whenTeamsHasNoHomeOrAwayScore(){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(arsenal));

        // tabelas vazais - arsenal não aparece em nenhuma delas
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(averagesService.findAllByCompetitionId(1L)).thenReturn(Map.of());

        averagesStep.sync(csc, tsc, ssc);

        ArgumentCaptor<List<Averages>> captor = ArgumentCaptor.forClass(List.class);

        verify(averagesService).saveAll(captor.capture());

        assertTrue(captor.getValue().isEmpty());
    }

    // cenário: misto - um novo, um atualizado.

    @Test
    void sync_shouldCreateAndUpdateAverages_whenSomeTeamsAreNewAndSomeExist(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team chelsea = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of(arsenal, chelsea));

        TableExternalResponse arsenalHome = this.buildTableEntry(100L, "Arsenal", 10, 4, 5);
        TableExternalResponse arsenalAway = buildTableEntry(100L, "Arsenal",  6, 5, 5);
        TableExternalResponse chelseaHome = buildTableEntry(200L, "Chelsea",  8, 6, 5);
        TableExternalResponse chelseaAway = buildTableEntry(200L, "Chelsea", 5, 7, 5);

        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(
                List.of(arsenalHome, chelseaHome),
                List.of(arsenalAway, chelseaAway)
        );

        Averages existingAverages = AveragesTestFactory.buildAverages(arsenal, competition);

        when(averagesService.findAllByCompetitionId(1L)).thenReturn(Map.of(1L, existingAverages));

        averagesStep.sync(csc, tsc, ssc);

        ArgumentCaptor<List<Averages>> captor = ArgumentCaptor.forClass(List.class);

        verify(averagesService).saveAll(captor.capture());

        List<Averages> saved = captor.getValue();
        assertEquals(2, saved.size());

        //Arsenal foi atualizado - Tem ID.
        Averages updated = saved.get(0);
        assertEquals(existingAverages.getId(), updated.getId());

        //Chelsea foi criado - n tem ID.
        Averages created = saved.get(1);
        assertNull(created.getId());

    }
}
