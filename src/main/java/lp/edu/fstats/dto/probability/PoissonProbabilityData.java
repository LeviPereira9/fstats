package lp.edu.fstats.dto.probability;

import java.math.BigDecimal;

public record PoissonProbabilityData(
        BigDecimal over05,
        BigDecimal over15,
        BigDecimal over25

) {}
