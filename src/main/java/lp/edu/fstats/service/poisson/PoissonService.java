package lp.edu.fstats.service.poisson;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.probability.PoissonProbabilityData;
import lp.edu.fstats.util.PoissonCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PoissonService {

    public PoissonProbabilityData calculate(BigDecimal lambda){
        BigDecimal p0 = PoissonCalculator.calculate(lambda, 0);
        BigDecimal p1 = PoissonCalculator.calculate(lambda, 1);
        BigDecimal p2 = PoissonCalculator.calculate(lambda, 2);
        BigDecimal p3 = PoissonCalculator.calculate(lambda, 3);

        BigDecimal over05 = BigDecimal.ONE.subtract(p0);

        BigDecimal over15 = BigDecimal.ONE.subtract(
                p0.add(p1)
        );

        BigDecimal over25 = BigDecimal.ONE.subtract(
                p0.add(p1).add(p2)
        );

        return new PoissonProbabilityData(
                over05,
                over15,
                over25
        );
    }
}
