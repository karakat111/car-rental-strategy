package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PricingContextTest {

    private final RentalRequest tenDays = new RentalRequest(
            new Car("E", "Corolla", "ECONOMY", 35.0), 10, CustomerType.REGULAR,
            LocalDate.of(2026, 10, 12), "U");

    @Test
    void delegatesToTheStrategyFromTheConstructor() {
        PricingContext ctx = new PricingContext(new StandardPricingStrategy());
        assertEquals(350.0, ctx.calculatePrice(tenDays), 0.01);
    }

    @Test
    void strategyCanBeReplacedAtRuntime() {
        PricingContext ctx = new PricingContext(new StandardPricingStrategy());
        double first = ctx.calculatePrice(tenDays);

        ctx.setStrategy(new LongTermPackagePricingStrategy());
        double second = ctx.calculatePrice(tenDays);

        assertEquals(350.0, first, 0.01);
        assertEquals(308.0, second, 0.01); // 7 * 35 + 3 * 35 * 0.6
        assertEquals("Long-term package (7 full + 60% extra days)", ctx.currentStrategyName());
    }

    @Test
    void usesExactlyWhatTheStrategyReturns() {
        PricingStrategy fixed = new PricingStrategy() {
            @Override
            public double calculatePrice(RentalRequest request) {
                return 42.0;
            }

            @Override
            public String name() {
                return "fixed";
            }
        };
        assertEquals(42.0, new PricingContext(fixed).calculatePrice(tenDays), 0.0);
    }

    @Test
    void nullStrategyIsRejected() {
        assertThrows(NullPointerException.class, () -> new PricingContext(null));
        PricingContext ctx = new PricingContext(new StandardPricingStrategy());
        assertThrows(NullPointerException.class, () -> ctx.setStrategy(null));
    }
}
