package lp.edu.fstats.steps;

import lp.edu.fstats.factory.apiResponse.FootballResponseFactory;
import lp.edu.fstats.factory.entity.CompTeamTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamsExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.TeamSyncStep;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.competition.CompetitionTeam;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.competition.CompetitionTeamRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.team.TeamService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamSyncStepTest {

    @Mock
    private TeamService teamService;

    @Mock
    private FootballApiClient footballApiClient;

    @Mock
    private CompetitionTeamRepository competitionTeamRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamSyncStep teamSyncStep;

    // cenário: times já salvos para a competição.
    @Test
    void sync_shouldReturnExistingTeams_whenCompetitionTeamsAlreadySaved(){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        CompetitionSyncContext csc = FootballResponseFactory.buildCsc(competition);

        Team team = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");

        CompetitionTeam competitionTeam = CompTeamTestFactory.buildCompetitionTeam(team, competition);

        when(competitionTeamRepository.findAllByCompetitionId(1L)).thenReturn(List.of(competitionTeam));

        TeamSyncContext result = teamSyncStep.sync(csc);

        assertNotNull(result);
        assertEquals(1, result.getTeams().size());
        assertEquals(team, result.getTeams().get(0));

        verifyNoInteractions(footballApiClient);
        verifyNoInteractions(teamService);
        verifyNoInteractions(teamRepository);

    }

    // cenario nenhum time salvo, todos são novos
    @Test
    void sync_shouldFetchSaveAndReturnAllTeams_whenNoTeamsSavedAndAllAreNew(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        CompetitionSyncContext csc = FootballResponseFactory.buildCsc(competition);

        CompetitionTeamExternalResponse externalArsenal = FootballResponseFactory
                .buildExternalTeam(100L, "Arsenal");

        CompetitionTeamExternalResponse externalChelsea = FootballResponseFactory
                .buildExternalTeam(200L, "Chelsea");

        CompetitionTeamsExternalResponse externalTeams = FootballResponseFactory
                .buildExternalTeams(List.of(externalArsenal, externalChelsea));

        Team savedArsenal = TeamTestFactory
                .buildTeam(1L, 100L, "Arsenal");

        Team savedChelsea = TeamTestFactory
                .buildTeam(2L, 200L, "Chelsea");

        when(competitionTeamRepository.findAllByCompetitionId(1L))
                .thenReturn(List.of());

        when(footballApiClient.getCurrentTeams(eq("PL"), any(Year.class)))
                .thenReturn(externalTeams);

        when(teamService.findAllByExternalId(List.of(100L, 200L)))
                .thenReturn(Map.of());

        when(teamRepository.saveAll(anyList()))
                .thenReturn(new ArrayList<>(List.of(savedArsenal, savedChelsea)));

        TeamSyncContext result = teamSyncStep.sync(csc);

        assertNotNull(result);
        assertEquals(2, result.getTeams().size());

        verify(teamRepository).saveAll(anyList());

        verify(competitionTeamRepository).saveAll(anyList());
    }

    // cenário 3
    @Test
    void sync_shouldSaveOnlyNewTeams_whenSomeTeamsAlreadyExistInDatabase(){
        Competition competition = CompetitionTestFactory
                .buildCompetition("PL");

        CompetitionSyncContext csc = FootballResponseFactory
                .buildCsc(competition);

        CompetitionTeamExternalResponse externalArsenal = FootballResponseFactory
                .buildExternalTeam(100L, "Arsenal");

        CompetitionTeamExternalResponse externalChelsea = FootballResponseFactory
                .buildExternalTeam(200L, "Chelsea");

        CompetitionTeamsExternalResponse externalTeams = FootballResponseFactory
                .buildExternalTeams(new ArrayList<>(List.of(externalArsenal, externalChelsea)));

        // Arsenal já existe no banco, Chelsea é novo
        Team existingArsenal = TeamTestFactory
                .buildTeam(1L, 100L, "Arsenal");

        Team savedChelsea = TeamTestFactory
                .buildTeam(2L, 200L, "Chelsea");

        when(competitionTeamRepository.findAllByCompetitionId(1L))
                .thenReturn(new ArrayList<>(List.of()));

        when(footballApiClient.getCurrentTeams(eq("PL"), any(Year.class)))
                .thenReturn(externalTeams);

        when(teamService.findAllByExternalId(List.of(100L, 200L)))
                .thenReturn(Map.of(100L, existingArsenal));

        when(teamRepository.saveAll(anyList()))
                .thenReturn(new ArrayList<>(new ArrayList<>(List.of(savedChelsea))));

        TeamSyncContext result = teamSyncStep.sync(csc);

        assertNotNull(result);
        assertEquals(2, result.getTeams().size());

        assertTrue(result.getTeams().contains(existingArsenal));
        assertTrue(result.getTeams().contains(savedChelsea));

        ArgumentCaptor<List<Team>> teamsCaptor = ArgumentCaptor.forClass(List.class);

        verify(teamRepository).saveAll(teamsCaptor.capture());

        assertEquals(1, teamsCaptor.getValue().size());
        assertEquals(200L, teamsCaptor.getValue().get(0).getExternalId());

    }

}
