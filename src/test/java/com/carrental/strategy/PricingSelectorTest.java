package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** One test per Selector rule, plus the rule priorities. */
class PricingSelectorTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 12);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 10, 10);
    private static final LocalDate TUESDAY = LocalDate.of(2026, 10, 13);

    private final PricingStrategySelector selector = new PricingStrategySelector();
    private final Car car = new Car("C1", "Civic", "COMPACT", 42.0);

    private RentalRequest request(int days, CustomerType type, LocalDate start) {
        return new RentalRequest(car, days, type, start, "U");
    }

    @Test
    void corporate_selectsLoyaltyTier() {
        assertInstanceOf(LoyaltyTierPricingStrategy.class,
                selector.select(request(3, CustomerType.CORPORATE, MONDAY)));
    }

    @Test
    void loyalty_selectsLoyaltyTier() {
        assertInstanceOf(LoyaltyTierPricingStrategy.class,
                selector.select(request(3, CustomerType.LOYALTY, MONDAY)));
    }

    @Test
    void longTerm_selectsLongTermPackage() {
        assertInstanceOf(LongTermPackagePricingStrategy.class,
                selector.select(request(10, CustomerType.REGULAR, MONDAY)));
    }

    @Test
    void sevenDays_isAlreadyLongTerm() {
        assertInstanceOf(LongTermPackagePricingStrategy.class,
                selector.select(request(7, CustomerType.REGULAR, TUESDAY)));
    }

    @Test
    void sixDays_isNotLongTerm() {
        assertInstanceOf(StandardPricingStrategy.class,
                selector.select(request(6, CustomerType.REGULAR, TUESDAY)));
    }

    @Test
    void weekendStart_selectsWeekendPremium() {
        assertInstanceOf(WeekendPremiumPricingStrategy.class,
                selector.select(request(2, CustomerType.REGULAR, SATURDAY)));
    }

    @Test
    void default_selectsStandard() {
        assertInstanceOf(StandardPricingStrategy.class,
                selector.select(request(3, CustomerType.REGULAR, TUESDAY)));
    }

    @Test
    void customerTypeWinsOverLongTermAndWeekend() {
        assertInstanceOf(LoyaltyTierPricingStrategy.class,
                selector.select(request(10, CustomerType.CORPORATE, SATURDAY)));
    }

    @Test
    void longTermWinsOverWeekend() {
        assertInstanceOf(LongTermPackagePricingStrategy.class,
                selector.select(request(8, CustomerType.REGULAR, SATURDAY)));
    }
}
