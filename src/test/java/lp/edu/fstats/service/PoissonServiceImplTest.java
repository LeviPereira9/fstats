package lp.edu.fstats.service;

import lp.edu.fstats.dto.probability.PoissonProbabilityData;
import lp.edu.fstats.service.poisson.PoissonServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class PoissonServiceImplTest {

    private final PoissonServiceImpl poissonService = new PoissonServiceImpl();

    // Tolerância para comparação de BigDecimal com arredondamento
    private static final BigDecimal DELTA = BigDecimal.valueOf(0.0001);

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        BigDecimal diff = expected.subtract(actual).abs();
        assertTrue(diff.compareTo(DELTA) < 0,
                "Esperado: " + expected + ", mas foi: " + actual);
    }

    @Test
    void calculate_shouldReturnCorrectProbabilities_whenLambdaIsOne() {
        BigDecimal lambda = BigDecimal.valueOf(1.0);

        PoissonProbabilityData result = poissonService.calculate(lambda);

        assertNotNull(result);
        assertBigDecimalEquals(BigDecimal.valueOf(0.632121), result.over05());
        assertBigDecimalEquals(BigDecimal.valueOf(0.264242), result.over15());
        assertBigDecimalEquals(BigDecimal.valueOf(0.080301), result.over25());
    }

    @Test
    void calculate_shouldReturnHigherProbabilities_whenLambdaIsHigh() {
        BigDecimal lambda = BigDecimal.valueOf(3.0); // média de 3 gols esperados

        PoissonProbabilityData result = poissonService.calculate(lambda);

        assertNotNull(result);
        // Com lambda alto, a chance de mais de 0.5, 1.5 e 2.5 gols deve ser bem maior
        assertTrue(result.over05().compareTo(BigDecimal.valueOf(0.9)) > 0);
        assertTrue(result.over15().compareTo(BigDecimal.valueOf(0.8)) > 0);
        assertTrue(result.over25().compareTo(BigDecimal.valueOf(0.5)) > 0);
    }

    @Test
    void calculate_shouldReturnLowProbabilities_whenLambdaIsLow() {
        BigDecimal lambda = BigDecimal.valueOf(0.1); // média muito baixa de gols

        PoissonProbabilityData result = poissonService.calculate(lambda);

        assertNotNull(result);
        // Com lambda muito baixo, é raro ter mais de 0.5 gol
        assertTrue(result.over05().compareTo(BigDecimal.valueOf(0.2)) < 0);
        assertTrue(result.over15().compareTo(BigDecimal.valueOf(0.05)) < 0);
    }

    @Test
    void calculate_shouldReturnDecreasingProbabilities_asThresholdIncreases() {
        BigDecimal lambda = BigDecimal.valueOf(1.5);

        PoissonProbabilityData result = poissonService.calculate(lambda);

        // over05 deve ser sempre maior que over15, que deve ser maior que over25
        // (é sempre mais provável ter mais de 0.5 gol do que mais de 2.5 gols)
        assertTrue(result.over05().compareTo(result.over15()) > 0);
        assertTrue(result.over15().compareTo(result.over25()) > 0);
    }
}