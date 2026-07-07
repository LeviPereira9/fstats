package lp.edu.fstats.service;

import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.service.competition.CompetitionServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceImplTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private CompetitionServiceImpl competitionService;


    //getCompetition

    @Test
    void getCompetition_shouldReturnCompetitionResponse_whenCompetitionExists(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        when(competitionRepository.findByCode("PL")).thenReturn(Optional.of(competition));

        CompetitionResponse response = competitionService.getCompetition("PL");

        assertNotNull(response);
        assertEquals("PL", response.code());
        assertEquals("Premier League", response.name());

        verify(competitionRepository).findByCode("PL");
    }

    @Test
    void getCompetition_shouldThrowNotFound_whenCompetitionDoesNotExist(){
        when(competitionRepository.findByCode("XX")).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> competitionService.getCompetition("XX"));
    }

    // saveCompetition

    @Test
    void saveCompetition_shouldReturnSavedCompetition(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        when(competitionRepository.save(competition)).thenReturn(competition);

        Competition result = competitionService.saveCompetition(competition);

        assertNotNull(result);
        assertEquals("PL", result.getCode());
        verify(competitionRepository).save(competition);
    }

}
