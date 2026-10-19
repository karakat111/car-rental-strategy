package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.pricing.PricingCalculator;
import com.carrental.strategy.pricing.PricingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Behaviour of the pricing family through PricingCalculator.
 * This file is identical on the BEFORE (switch) and the AFTER (Strategy) commit.
 */
class PricingBehaviorTest {

    private static final Car ECONOMY = new Car("C1", "Corolla", "ECONOMY", 40.0);
    private static final Car LUXURY = new Car("L1", "BMW", "LUXURY", 100.0);
    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 12);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 10, 10);

    private final PricingCalculator calculator = new PricingCalculator();

    private double price(PricingMode mode, RentalRequest request) {
        calculator.setMode(mode);
        return calculator.calculate(request);
    }

    @Test
    void standard_isRateTimesDays() {
        RentalRequest req = new RentalRequest(ECONOMY, 5, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(200.0, price(PricingMode.STANDARD, req), 0.01);
    }

    @Test
    void weekendPremium_chargesMoreOnSaturdayAndSunday() {
        // Sat, Sun, Mon: 54 + 54 + 40
        RentalRequest req = new RentalRequest(ECONOMY, 3, CustomerType.REGULAR, SATURDAY, "U1");
        assertEquals(148.0, price(PricingMode.WEEKEND_PREMIUM, req), 0.01);
    }

    @Test
    void longTerm_firstSevenDaysFullThenSixtyPercent() {
        RentalRequest ten = new RentalRequest(ECONOMY, 10, CustomerType.REGULAR, MONDAY, "U1");
        RentalRequest seven = new RentalRequest(ECONOMY, 7, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(352.0, price(PricingMode.LONG_TERM, ten), 0.01);   // 280 + 3 * 24
        assertEquals(280.0, price(PricingMode.LONG_TERM, seven), 0.01); // no extra days yet
    }

    @Test
    void loyaltyTier_corporateGetsFifteenPercentOnAnyCar() {
        RentalRequest req = new RentalRequest(LUXURY, 2, CustomerType.CORPORATE, MONDAY, "CORP");
        assertEquals(170.0, price(PricingMode.LOYALTY_TIER, req), 0.01);
    }

    @Test
    void loyaltyTier_loyaltyGetsTenPercentOnlyOnEconomyAndCompact() {
        RentalRequest eco = new RentalRequest(ECONOMY, 2, CustomerType.LOYALTY, MONDAY, "L");
        RentalRequest lux = new RentalRequest(LUXURY, 2, CustomerType.LOYALTY, MONDAY, "L");
        assertEquals(72.0, price(PricingMode.LOYALTY_TIER, eco), 0.01);
        assertEquals(200.0, price(PricingMode.LOYALTY_TIER, lux), 0.01);
    }

    @Test
    void loyaltyTier_regularCustomerPaysFullPrice() {
        RentalRequest req = new RentalRequest(ECONOMY, 2, CustomerType.REGULAR, MONDAY, "U");
        assertEquals(80.0, price(PricingMode.LOYALTY_TIER, req), 0.01);
    }

    @Test
    void modeCanBeChangedWhileRunning() {
        RentalRequest req = new RentalRequest(ECONOMY, 10, CustomerType.REGULAR, MONDAY, "U1");
        assertEquals(400.0, price(PricingMode.STANDARD, req), 0.01);
        assertEquals(352.0, price(PricingMode.LONG_TERM, req), 0.01);
    }

    @ParameterizedTest
    @EnumSource(PricingMode.class)
    void priceIsNeverNegative(PricingMode mode) {
        for (int days = 1; days <= 60; days++) {
            for (CustomerType type : CustomerType.values()) {
                RentalRequest onMonday = new RentalRequest(ECONOMY, days, type, MONDAY, "U");
                RentalRequest onSaturday = new RentalRequest(LUXURY, days, type, SATURDAY, "U");
                assertTrue(price(mode, onMonday) >= 0.0, mode + " " + days + " days");
                assertTrue(price(mode, onSaturday) >= 0.0, mode + " " + days + " days");
            }
        }
    }

    @Test
    void invalidRentalDaysAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RentalRequest(ECONOMY, 0, CustomerType.REGULAR, MONDAY, "U"));
    }
}
