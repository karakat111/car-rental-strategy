package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

/**
 * AFTER refactoring: the mode-based entry point stays, but the switch is gone.
 * The mode builds its strategy and a PricingContext runs it.
 * Kept so the same behaviour tests pass on the BEFORE and the AFTER commit.
 */
public class PricingCalculator {

    private final PricingContext context = new PricingContext(PricingMode.STANDARD.createStrategy());

    public void setMode(PricingMode mode) {
        context.setStrategy(mode.createStrategy());
    }

    public double calculate(RentalRequest request) {
        return context.calculatePrice(request);
    }
}
