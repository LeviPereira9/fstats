package lp.edu.fstats.dto.averages;

import lp.edu.fstats.model.avarages.Averages;

import java.math.BigDecimal;

public record AverageResponse(
        String teamName,
        BigDecimal avgGoalsForHome,
        BigDecimal avgGoalsAgainstHome,
        BigDecimal avgGoalsForAway,
        BigDecimal avgGaolsAgainstAway
) {
    public AverageResponse(Averages source){
        this(
                source.getTeam().getShortName(),
                source.getAvgGoalsForHome(),
                source.getAvgGoalsAgainstHome(),
                source.getAvgGoalsForAway(),
                source.getAvgGoalsForAway()
        );
    }
}
