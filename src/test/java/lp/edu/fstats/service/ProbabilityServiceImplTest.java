package lp.edu.fstats.service;

import lp.edu.fstats.factory.entity.ProbabilityTestFactory;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.repository.probability.ProbabilityRepository;
import lp.edu.fstats.service.probability.ProbabilityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProbabilityServiceImplTest {

    @Mock
    private ProbabilityRepository probabilityRepository;

    @InjectMocks
    private ProbabilityServiceImpl probabilityService;


    //saveAll
    @Test
    void saveAll_shouldCallRepositorySaveAll_withGivenProbabilities(){
        List<Probability> probabilities = List.of(
                ProbabilityTestFactory.buildProbability(
                        BigDecimal.valueOf(0.9),
                        BigDecimal.valueOf(0.7),
                        BigDecimal.valueOf(0.5)
                )
        );

        probabilityService.saveAll(probabilities);

        verify(probabilityRepository).saveAll(probabilities);
    }

    @Test
    void saveAll_shouldCallRepositorySaveAll_whenListIsEmpty(){
        List<Probability> probabilities = List.of();

        probabilityService.saveAll(probabilities);

        verify(probabilityRepository).saveAll(probabilities);
    }
}
