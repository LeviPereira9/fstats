package lp.edu.fstats.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PoissonCalculator {
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private static BigDecimal exp(BigDecimal lambda) {
        return BigDecimal.valueOf(Math.exp(lambda.negate().doubleValue()));
    }

    private static BigDecimal factorial(int n) {
        BigDecimal result = BigDecimal.ONE;

        for(int i = 1; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }

        return result;
    }

    public static BigDecimal calculate(BigDecimal lambda, int k) {
        BigDecimal e = exp(lambda);
        BigDecimal lambdaPow = lambda.pow(k, MC);
        BigDecimal factorial = factorial(k);

        return e
                .multiply(lambdaPow,MC)
                .divide(factorial, 6, RoundingMode.HALF_UP);
    }
}
