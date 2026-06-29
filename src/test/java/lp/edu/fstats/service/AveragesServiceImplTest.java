package lp.edu.fstats.service;

import lp.edu.fstats.dto.averages.AverageResponse;
import lp.edu.fstats.dto.averages.AveragesResponse;
import lp.edu.fstats.factory.TeamTestFactory;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.averages.AveragesRepository;
import lp.edu.fstats.service.averages.AveragesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AveragesServiceImplTest {

    @Mock
    private AveragesRepository averagesRepository;

    @InjectMocks
    private AveragesServiceImpl averagesService;

    //helpers
    private Team buildTeam(Long id, String name){
        return TeamTestFactory.buildTeam(id, 10L, name);
    }

    private Averages buildAverages(Team team){
        Averages averages = new Averages();
        averages.setId(1L);
        averages.setTeam(team);

        averages.setAvgGoalsForHome(BigDecimal.valueOf(1.5));
        averages.setAvgGoalsAgainstHome(BigDecimal.valueOf(0.8));
        averages.setAvgGoalsForAway(BigDecimal.valueOf(1.1));
        averages.setAvgGoalsAgainstAway(BigDecimal.valueOf(1.3));

        return averages;
    }

    //findAllByCompetitionId
    @Test
    void findAllByCompetitionId_shouldReturnMapKeyedByTeamId_whenAveragesExist(){

        Team team1 = this.buildTeam(1L, "Arsenal");
        Team team2 = this.buildTeam(2L, "Chelsea");

        Averages avg1 = this.buildAverages(team1);
        Averages avg2 = this.buildAverages(team2);

        when(averagesRepository.findAllByCompetition_Id(10L)).thenReturn(List.of(avg1, avg2));

        Map<Long, Averages> result = averagesService.findAllByCompetitionId(10L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(avg1, result.get(1L));
        assertEquals(avg2, result.get(2L));

    }

    @Test
    void findAllByCompetitionId_shouldReturnEmptyMap_whenNoAveragesFound(){
        when(averagesRepository.findAllByCompetition_Id(11L)).thenReturn(List.of());

        Map<Long, Averages> result = averagesService.findAllByCompetitionId(11L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //findAllByCompetition

    @Test
    void findAllByCompetition_shouldReturnAveragesResponse_whenAveragesExist(){
        Team team = this.buildTeam(1L, "Arsenal");
        Averages avg1 = this.buildAverages(team);

        when(averagesRepository.findAllByCompetition_Id(10l)).thenReturn(List.of(avg1));

        AveragesResponse response = averagesService.findAllByCompetition(10L);

        assertNotNull(response);
        assertEquals(1, response.averages().size());

        AverageResponse averageResponse = response.averages().get(0);

        assertEquals("Arsenal", averageResponse.teamName());

        assertEquals(BigDecimal.valueOf(1.5).multiply(BigDecimal.valueOf(100)), averageResponse.avgGoalsForHome());
        assertEquals(BigDecimal.valueOf(0.8).multiply(BigDecimal.valueOf(100)), averageResponse.avgGoalsAgainstHome());
        assertEquals(BigDecimal.valueOf(1.1).multiply(BigDecimal.valueOf(100)), averageResponse.avgGoalsForAway());
        assertEquals(BigDecimal.valueOf(1.3).multiply(BigDecimal.valueOf(100)), averageResponse.avgGaolsAgainstAway());
    }

    @Test
    void findAllByCompetition_shouldReturnEmptyResponse_whenNoAveragesFound(){

        when(averagesRepository.findAllByCompetition_Id(10L)).thenReturn(List.of());

        AveragesResponse response = averagesService.findAllByCompetition(10L);

        assertNotNull(response);
        assertTrue(response.averages().isEmpty());

    }

    //saveAll

    @Test
    void saveAll_shouldCallRepositorySaveAll_withGivenAverages(){
        Team team = this.buildTeam(1L, "Arsenal");
        Averages avg1 = this.buildAverages(team);

        List<Averages> averagesList = List.of(avg1);

        averagesService.saveAll(averagesList);

        verify(averagesRepository).saveAll(averagesList);
    }

}