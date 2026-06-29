package lp.edu.fstats.factory;

import lp.edu.fstats.model.probability.Probability;

import java.math.BigDecimal;

public class ProbabilityTestFactory {

    public static Probability buildProbability(BigDecimal over05, BigDecimal over15, BigDecimal over25){

        Probability probability = new Probability();
        probability.setId(1L);
        probability.setProbabilityOver05(over05);
        probability.setProbabilityOver15(over15);
        probability.setProbabilityOver25(over25);
        probability.setMatchDay(1);

        return probability;
    }

}
