package lp.edu.fstats.util;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PoissonCalculatorTest {

    private static final BigDecimal DELTA = BigDecimal.valueOf(0.000001);

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {

        BigDecimal diff = expected.subtract(actual).abs();
        assertTrue(diff.compareTo(DELTA) < 0,
                "Esperado: " + expected + ", mas foi: " + actual);

    }

    // factorial (testada indiretamente via calculate
    @Test
    void calculate_shouldReturnCorrectValue_whenKIsZero(){
        // P(0) = e^(-λ) * λ^0 / 0! = e^(-λ)
        // Para λ=1: P(0) = e^-1 ≈ 0.367879
        BigDecimal result = PoissonCalculator.calculate(BigDecimal.ONE, 0);

        assertBigDecimalEquals(BigDecimal.valueOf(0.367879), result);
    }

    @Test
    void calculate_shouldReturnCorrectValue_whenKIsTwo(){
        // P(2) = e^(-λ) * λ^2 / 2! = e^-1 * 1 / 2 ≈ 0.183940
        BigDecimal result = PoissonCalculator.calculate(BigDecimal.ONE, 2);

        assertBigDecimalEquals(BigDecimal.valueOf(0.183940), result);
    }

    @Test
    void calculate_shouldReturnCorrectValue_whenKIsThree(){
        // P(3) = e^(-λ) * λ^3 / 3! = e^-1 * 1 / 6 ≈ 0.061313
        BigDecimal result = PoissonCalculator.calculate(BigDecimal.ONE, 3);

        assertBigDecimalEquals(BigDecimal.valueOf(0.061313), result);
    }

    @Test
    void calculate_shouldReturnOne_whenLambdaIsZeroAndKIsZero(){
        // P(0) com λ=0: e^0 * 0^0 / 0! = 1 * 1 / 1 = 1
        BigDecimal result = PoissonCalculator.calculate(BigDecimal.ZERO, 0);

        assertBigDecimalEquals(BigDecimal.ONE, result);
    }

    @Test
    void calculate_shouldReturnZero_whenLambdaIsZeroAndKIsGreaterThanZero(){
        // P(k) com λ=0 e k>0: e^0 * 0^k / k! = 1 * 0 / k! = 0

        BigDecimal result = PoissonCalculator.calculate(BigDecimal.ZERO, 1);

        assertBigDecimalEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculate_shouldDecreaseForIncreasingK_whenLambdaIsPointFive(){

        BigDecimal lambda = BigDecimal.valueOf(0.5);

        BigDecimal p0 = PoissonCalculator.calculate(lambda, 0);

        BigDecimal p1 = PoissonCalculator.calculate(lambda, 1);

        BigDecimal p2 = PoissonCalculator.calculate(lambda, 2);

        BigDecimal p3 = PoissonCalculator.calculate(lambda, 3);

        assertTrue(p0.compareTo(p1) > 0);
        assertTrue(p1.compareTo(p2) > 0);
        assertTrue(p2.compareTo(p3) > 0);

    }

    @Test
    void calculate_shouldSumToOne_acrossAllKValues(){
        // a soma de todas as probabilidades de Poisson deve convergir para 1
        // testamos com k de 0 a 20 — suficiente para λ=2

        BigDecimal lambda = BigDecimal.valueOf(2.0);
        BigDecimal sum = BigDecimal.ZERO;

        for(int k = 0; k <= 20; k++){
            sum = sum.add(PoissonCalculator.calculate(lambda, k));
        }

        //deve ser muito próximo de 1
        BigDecimal diff = BigDecimal.ONE.subtract(sum).abs();

        assertTrue(diff.compareTo(BigDecimal.valueOf(0.0001)) < 0, "Soma esparada próxima de 1, mas foi: " + sum);
    }

}
