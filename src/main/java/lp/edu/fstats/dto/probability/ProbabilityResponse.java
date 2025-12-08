package lp.edu.fstats.dto.probability;

import lp.edu.fstats.model.probability.Probability;

import java.math.BigDecimal;

public record ProbabilityResponse(
        BigDecimal over05,
        BigDecimal over15,
        BigDecimal over25
) {
    public ProbabilityResponse(Probability source) {

        this(
                source.getProbabilityOver05().multiply(BigDecimal.valueOf(100)),
                source.getProbabilityOver15().multiply(BigDecimal.valueOf(100)),
                source.getProbabilityOver25().multiply(BigDecimal.valueOf(100))
        );
    }
}
