package lp.edu.fstats.steps;

import lp.edu.fstats.factory.apiResponse.FootballResponseFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.competition.CurrentSeasonExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.CompetitionSyncStep;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.service.competition.CompetitionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompetitionSyncStepTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private FootballApiClient footballApiClient;

    @InjectMocks
    private CompetitionSyncStep competitionSyncStep;

    // helpers
    private Competition buildSavedCompetition(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        competition.setId(1L);
        competition.setExternalId(100L);
        competition.setStartDate(LocalDate.of(2023, 8, 1));
        competition.setStartDate(LocalDate.of(2024, 5, 1));
        competition.setApiCurrentMatchDay(20);

        return competition;
    }

    // cenário: competição nova, temporada em andamento.

    @Test
    void sync_shouldCreateNewCompetition_whenNoSavedCompetitionAndSeasonIsOngoing(){
        CompetitionExternalResponse externalResponse = FootballResponseFactory.buildExternalCompetitionResponse(200L, 5);

        when(competitionRepository.findByCodeAndStatus("PL")).thenReturn(Optional.empty());

        when(footballApiClient.getCurrentCompetition("PL")).thenReturn(externalResponse);

        when(competitionRepository.existsByExternalId(200L)).thenReturn(false);

        CompetitionSyncContext context = competitionSyncStep.sync("PL", Year.of(2024));

        assertNotNull(context);
        assertNotNull(context.getCompetition());
        assertEquals("PL", context.getCompetition().getCode());
        assertEquals(200L, context.getCompetition().getExternalId());
    }

    // cenário: competição nova, temporada já finalizada
    @Test
    void sync_shouldReturnNullCompetition_whenNoSavedCompetitionAndSeasonIsFinished(){
        CompetitionExternalResponse externalResponse = FootballResponseFactory.buildExternalCompetitionResponse(200L, 38);

        when(competitionRepository.findByCodeAndStatus("PL")).thenReturn(Optional.empty());

        when(footballApiClient.getCurrentCompetition("PL")).thenReturn(externalResponse);

        when(competitionRepository.existsByExternalId(200L)).thenReturn(true);

        CompetitionSyncContext context = competitionSyncStep.sync("PL", Year.of(2024));

        assertNotNull(context);
        assertNull(context.getCompetition());

        verify(competitionService, never()).saveCompetition(any());
    }

    // cenário competição já existe, atualize
    @Test
    void sync_shouldUpdateExistingCompetition_whenSavedCompetitionExists(){
        Competition savedCompetition = this.buildSavedCompetition();

        CompetitionExternalResponse externalResponse = FootballResponseFactory.buildExternalCompetitionResponse(100L, 21);

        when(competitionRepository.findByCodeAndStatus("PL")).thenReturn(Optional.of(savedCompetition));

        when(footballApiClient.getCurrentCompetition("PL")).thenReturn(externalResponse);

        CompetitionSyncContext context = competitionSyncStep.sync("PL", Year.of(2024));

        assertNotNull(context);

        assertEquals(savedCompetition, context.getCompetition());
        assertEquals(21, context.getCompetition().getApiCurrentMatchDay());

        verify(competitionService).saveCompetition(savedCompetition);
        verify(competitionRepository, never()).existsByExternalId(any());

    }

}
