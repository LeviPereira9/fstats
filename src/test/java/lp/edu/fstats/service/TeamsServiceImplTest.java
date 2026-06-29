package lp.edu.fstats.service;


import lp.edu.fstats.factory.TeamTestFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.team.TeamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TeamsServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;



    //findAllByExternalId
    @Test
    void findAllByExternalId_shouldReturnMapKeyedByExternalId_whenTeamExist(){
        Team team1 = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team team2 = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        List<Long> externalIds = List.of(100L, 200L);

        when(teamRepository.findAllByExternalIdIn(externalIds)).thenReturn(List.of(team1, team2));

        Map<Long, Team> result = teamService.findAllByExternalId(externalIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(team1, result.get(100L));
        assertEquals(team2, result.get(200L));

        verify(teamRepository, times(1)).findAllByExternalIdIn(externalIds);
    }

    @Test
    void findAllByExternalId_shouldReturnEmptyMap_whenNoTeamsFound(){
        List<Long> externalIds = List.of(999L);

        when(teamRepository.findAllByExternalIdIn(externalIds)).thenReturn(Collections.emptyList());

        Map<Long, Team> result = teamService.findAllByExternalId(externalIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(teamRepository, times(1)).findAllByExternalIdIn(externalIds);
    }

    //saveTeams
    @Test
    void saveTeams_shouldCallSaveAll_withGivenTeAMS(){
        List<Team> teams = List.of(
                TeamTestFactory.buildTeam(1L, 100L, "Arsenal"),
                TeamTestFactory.buildTeam(2L, 200L, "Chelsea")
        );

        teamService.saveTeams(teams);

        verify(teamRepository).saveAll(teams);
    }
}
