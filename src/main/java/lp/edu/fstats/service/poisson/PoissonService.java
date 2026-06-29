package lp.edu.fstats.service.poisson;

import lp.edu.fstats.dto.probability.PoissonProbabilityData;

import java.math.BigDecimal;

public interface PoissonService {

    PoissonProbabilityData calculate(BigDecimal lambda);

}
