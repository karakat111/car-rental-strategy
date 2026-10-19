package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.deposit.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DepositSelectorAndContextTest {

    private final CreditScoreProvider scores = customerId -> 700;
    private final DepositStrategySelector selector = new DepositStrategySelector(scores);
    private final LocalDate day = LocalDate.of(2026, 10, 15);
    private final Car economy = new Car("E", "Corolla", "ECONOMY", 35.0);
    private final Car luxury = new Car("L", "BMW", "LUXURY", 120.0);

    @Test
    void corporate_getsCategoryFlat() {
        RentalRequest req = new RentalRequest(economy, 3, CustomerType.CORPORATE, day, "CORP");
        assertInstanceOf(CategoryFlatDepositStrategy.class, selector.select(req));
    }

    @Test
    void luxuryNonCorporate_getsRiskBased() {
        RentalRequest req = new RentalRequest(luxury, 3, CustomerType.REGULAR, day, "U");
        assertInstanceOf(RiskBasedDepositStrategy.class, selector.select(req));
    }

    @Test
    void regularEconomy_getsPercentage() {
        RentalRequest req = new RentalRequest(economy, 3, CustomerType.REGULAR, day, "U");
        assertInstanceOf(PercentageOfPriceDepositStrategy.class, selector.select(req));
    }

    @Test
    void corporateWinsOverLuxury() {
        RentalRequest req = new RentalRequest(luxury, 3, CustomerType.CORPORATE, day, "CORP");
        assertInstanceOf(CategoryFlatDepositStrategy.class, selector.select(req));
    }

    @Test
    void depositContext_delegatesToCurrentStrategy() {
        RentalRequest req = new RentalRequest(economy, 2, CustomerType.REGULAR, day, "U");
        DepositContext ctx = new DepositContext(new PercentageOfPriceDepositStrategy());
        assertEquals(25.0, ctx.calculateDeposit(req, 100.0), 0.01);

        ctx.setStrategy(new CategoryFlatDepositStrategy());

        assertEquals(100.0, ctx.calculateDeposit(req, 100.0), 0.01);
        assertEquals("Flat by category (100-500)", ctx.currentStrategyName());
    }

    @Test
    void nullStrategyIsRejected() {
        assertThrows(NullPointerException.class, () -> new DepositContext(null));
        DepositContext ctx = new DepositContext(new PercentageOfPriceDepositStrategy());
        assertThrows(NullPointerException.class, () -> ctx.setStrategy(null));
    }
}
