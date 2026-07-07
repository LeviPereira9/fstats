package lp.edu.fstats.service;

import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.service.standings.StandingsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StandingsServiceImplTest {

    @Mock
    private StandingsRepository standingsRepository;

    @InjectMocks
    private StandingsServiceImpl standingsService;

    //helpers
    private Standings buildStandings(Team team, Integer position, Integer points){
        Standings standings = new Standings();

        standings.setId(1L);
        standings.setTeam(team);
        standings.setPosition(position);
        standings.setPlayedGames(10);
        standings.setForm("WWDLW");
        standings.setWon(6);
        standings.setDraw(2);
        standings.setLost(2);
        standings.setPoints(points);
        standings.setGoalsFor(20);
        standings.setGoalsAgainst(10);
        standings.setGoalDifference(10);

        return standings;
    }

    // findAllByCompetitionId
    @Test
    void findAllByCompetitionId_shouldReturnMapKeyedByTeamExternalId_whenStandingsExist(){

        Team team1 = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team team2 = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        Standings standings1 = this.buildStandings(team1, 1, 30);
        Standings standings2 = this.buildStandings(team2, 2, 28);

        when(standingsRepository.findAllByCompetition_Id(1L)).thenReturn(List.of(standings1, standings2));

        Map<Long, Standings> result = standingsService.findAllByCompetitionId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(standings1, result.get(100L));
        assertEquals(standings2, result.get(200L));
    }

    @Test
    void findAllByCompetitionId_shouldReturnEmptyMap_whenNoStandingsFound(){

        when(standingsRepository.findAllByCompetition_Id(1L)).thenReturn(List.of());

        Map<Long, Standings> result = standingsService.findAllByCompetitionId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //getStandings
    @Test
    void getStandings_shouldReturnStandingsResponse_whenStandingsExist(){

        Team team = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");

        Standings standings = this.buildStandings(team, 1, 30);

        when(standingsRepository.findAllByCompetition_Id(1L)).thenReturn(List.of(standings));

        StandingsResponse response = standingsService.getStandings(1L);

        assertNotNull(response);
        assertEquals(1, response.standings().size());
        assertEquals("Arsenal", response.standings().get(0).teamShortName());
        assertEquals(1, response.standings().get(0).position());

    }

    @Test
    void getStandings_shouldThrowNotFound_whenStandingsListIsEmpty(){
        when(standingsRepository.findAllByCompetition_Id(1L)).thenReturn(List.of());

        assertThrows(CustomNotFoundException.class,
                ()-> standingsService.getStandings(1L));
    }

    //saveAll
    @Test
    void saveAll_shouldCallRepositorySaveAll_wthGivenStandings(){
        Team team = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        List<Standings> standingsList = List.of(this.buildStandings(team, 1, 30));

        standingsService.saveAll(standingsList);

        verify(standingsRepository).saveAll(standingsList);
    }

}
