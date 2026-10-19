package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.deposit.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DepositStrategyTest {

    private final Car economy = new Car("E1", "Corolla", "ECONOMY", 35.0);
    private final Car luxury = new Car("L1", "BMW", "LUXURY", 120.0);
    private final LocalDate day = LocalDate.of(2026, 10, 15);

    /** Fake data source: always returns the score it was given. */
    static class FixedCreditScore implements CreditScoreProvider {
        private final int score;

        FixedCreditScore(int score) {
            this.score = score;
        }

        @Override
        public int getScore(String customerId) {
            return score;
        }
    }

    @Test
    void percentageOfPrice_is25Percent() {
        RentalRequest req = new RentalRequest(economy, 3, CustomerType.REGULAR, day, "U");
        assertEquals(50.0, new PercentageOfPriceDepositStrategy().calculateDeposit(req, 200.0), 0.01);
    }

    @Test
    void categoryFlat_dependsOnCategoryOnly() {
        RentalRequest eco = new RentalRequest(economy, 10, CustomerType.REGULAR, day, "U");
        RentalRequest lux = new RentalRequest(luxury, 1, CustomerType.REGULAR, day, "U");
        assertEquals(100.0, new CategoryFlatDepositStrategy().calculateDeposit(eco, 999.0), 0.01);
        assertEquals(500.0, new CategoryFlatDepositStrategy().calculateDeposit(lux, 10.0), 0.01);
    }

    @Test
    void categoryFlat_unknownCategoryUsesDefaultAmount() {
        Car van = new Car("V1", "Van", "VAN", 80.0);
        RentalRequest req = new RentalRequest(van, 2, CustomerType.REGULAR, day, "U");
        assertEquals(200.0, new CategoryFlatDepositStrategy().calculateDeposit(req, 50.0), 0.01);
    }

    @Test
    void riskBased_highScore_lowersDeposit() {
        RentalRequest req = new RentalRequest(economy, 3, CustomerType.REGULAR, day, "GOOD");
        double highDep = new RiskBasedDepositStrategy(new FixedCreditScore(800)).calculateDeposit(req, 200.0);
        double lowDep = new RiskBasedDepositStrategy(new FixedCreditScore(400)).calculateDeposit(req, 200.0);
        assertTrue(highDep < lowDep);
        assertEquals(100.0, lowDep, 0.01);  // 0.50 * 200
        assertEquals(50.0, highDep, 0.01);  // 0.15 * 200 = 30, category floor 50
    }

    @Test
    void riskBased_middleBandsUseTheirOwnFactor() {
        RentalRequest req = new RentalRequest(luxury, 3, CustomerType.REGULAR, day, "U");
        assertEquals(350.0, new RiskBasedDepositStrategy(new FixedCreditScore(600)).calculateDeposit(req, 1000.0), 0.01);
        assertEquals(250.0, new RiskBasedDepositStrategy(new FixedCreditScore(700)).calculateDeposit(req, 1000.0), 0.01);
    }

    @Test
    void riskBased_unknownScore_isCautious() {
        RentalRequest req = new RentalRequest(luxury, 2, CustomerType.REGULAR, day, "X");
        DepositStrategy strategy = new RiskBasedDepositStrategy(new FixedCreditScore(-1));
        // 0.50 * 400 = 200, floor 250 -> 250
        assertEquals(250.0, strategy.calculateDeposit(req, 400.0), 0.01);
    }

    @Test
    void riskBased_requiresAProvider() {
        assertThrows(NullPointerException.class, () -> new RiskBasedDepositStrategy(null));
    }

    @Test
    void simulatedProvider_returnsStableScoreInRange() {
        CreditScoreProvider provider = new SimulatedCreditScoreProvider();
        int score = provider.getScore("CUST-001");
        assertEquals(score, provider.getScore("CUST-001"));
        assertTrue(score >= 300 && score <= 850);
        assertEquals(-1, provider.getScore(" "));
        assertEquals(-1, provider.getScore(null));
    }

    static Stream<DepositStrategy> allDepositStrategies() {
        return Stream.of(
                new PercentageOfPriceDepositStrategy(),
                new CategoryFlatDepositStrategy(),
                new RiskBasedDepositStrategy(new FixedCreditScore(700)));
    }

    @ParameterizedTest
    @MethodSource("allDepositStrategies")
    void depositIsNeverNegative(DepositStrategy strategy) {
        for (double price = 0; price <= 5000; price += 250) {
            RentalRequest req = new RentalRequest(economy, 2, CustomerType.REGULAR, day, "U");
            assertTrue(strategy.calculateDeposit(req, price) >= 0, strategy.name() + " price " + price);
        }
    }
}
