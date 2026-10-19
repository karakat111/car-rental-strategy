package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {

    private static final Car ECONOMY = new Car("C1", "Corolla", "ECONOMY", 40.0);
    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 12);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 10, 10);

    @Test
    void standard_isSimpleProduct() {
        RentalRequest req = new RentalRequest(ECONOMY, 5, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(200.0, new StandardPricingStrategy().calculatePrice(req), 0.01);
    }

    @Test
    void weekendPremium_chargesMoreOnWeekendDays() {
        // 3 days starting Saturday: Sat, Sun, Mon -> 2 weekend days + 1 weekday
        RentalRequest req = new RentalRequest(ECONOMY, 3, CustomerType.REGULAR, SATURDAY, "U1");
        double price = new WeekendPremiumPricingStrategy().calculatePrice(req);
        assertEquals(40 * 1.35 + 40 * 1.35 + 40, price, 0.01);
        assertTrue(price > new StandardPricingStrategy().calculatePrice(req));
    }

    @Test
    void weekendPremium_weekdaysOnlyCostTheSameAsStandard() {
        RentalRequest req = new RentalRequest(ECONOMY, 3, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(120.0, new WeekendPremiumPricingStrategy().calculatePrice(req), 0.01);
    }

    @Test
    void longTerm_fullThenDiscountedDays() {
        RentalRequest req = new RentalRequest(ECONOMY, 10, CustomerType.REGULAR, MONDAY, "U1");
        // 7 * 40 + 3 * 40 * 0.6 = 280 + 72
        assertEquals(352.0, new LongTermPackagePricingStrategy().calculatePrice(req), 0.01);
    }

    @Test
    void longTerm_sevenDaysOrFewerHaveNoDiscount() {
        RentalRequest req = new RentalRequest(ECONOMY, 7, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(280.0, new LongTermPackagePricingStrategy().calculatePrice(req), 0.01);
    }

    @Test
    void loyaltyTier_corporateGetsFifteenPercent() {
        RentalRequest req = new RentalRequest(ECONOMY, 4, CustomerType.CORPORATE, MONDAY, "CORP");
        assertEquals(136.0, new LoyaltyTierPricingStrategy().calculatePrice(req), 0.01);
    }

    @Test
    void loyaltyTier_loyaltyOnlyOnEconomyAndCompact() {
        Car luxury = new Car("L1", "BMW", "LUXURY", 100.0);
        RentalRequest eco = new RentalRequest(ECONOMY, 2, CustomerType.LOYALTY, MONDAY, "L");
        RentalRequest lux = new RentalRequest(luxury, 2, CustomerType.LOYALTY, MONDAY, "L");
        assertEquals(72.0, new LoyaltyTierPricingStrategy().calculatePrice(eco), 0.01);
        assertEquals(200.0, new LoyaltyTierPricingStrategy().calculatePrice(lux), 0.01);
    }

    @Test
    void loyaltyTier_regularCustomerPaysFullPrice() {
        RentalRequest req = new RentalRequest(ECONOMY, 2, CustomerType.REGULAR, MONDAY, "U");
        assertEquals(80.0, new LoyaltyTierPricingStrategy().calculatePrice(req), 0.01);
    }

    static Stream<PricingStrategy> allPricingStrategies() {
        return Stream.of(
                new StandardPricingStrategy(),
                new WeekendPremiumPricingStrategy(),
                new LongTermPackagePricingStrategy(),
                new LoyaltyTierPricingStrategy());
    }

    @ParameterizedTest
    @MethodSource("allPricingStrategies")
    void priceIsNeverNegative(PricingStrategy strategy) {
        for (int days = 1; days <= 60; days++) {
            for (CustomerType type : CustomerType.values()) {
                RentalRequest req = new RentalRequest(ECONOMY, days, type, SATURDAY, "U");
                assertTrue(strategy.calculatePrice(req) >= 0, strategy.name() + " " + days + " days");
            }
        }
    }

    @ParameterizedTest
    @MethodSource("allPricingStrategies")
    void everyStrategyHasAName(PricingStrategy strategy) {
        assertTrue(strategy.name() != null && !strategy.name().isBlank());
    }
}
