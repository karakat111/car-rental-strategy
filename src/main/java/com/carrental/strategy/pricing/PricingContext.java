package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

import java.util.Objects;

/**
 * Context for the pricing strategy family.
 * Holds a strategy, allows runtime replacement, delegates the work.
 * Contains no if/switch/instanceof on the strategy type.
 */
public class PricingContext {

    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public PricingStrategy getStrategy() {
        return strategy;
    }

    public double calculatePrice(RentalRequest request) {
        return strategy.calculatePrice(request);
    }

    public String currentStrategyName() {
        return strategy.name();
    }
}
