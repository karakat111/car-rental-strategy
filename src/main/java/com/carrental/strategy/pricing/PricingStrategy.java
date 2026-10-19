package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

/**
 * Strategy interface - family 1: how to calculate the rental price.
 * Implementations differ in logic, not only in a constant factor.
 */
public interface PricingStrategy {

    /** @return total rental price (never negative) */
    double calculatePrice(RentalRequest request);

    /** Human-readable name for demos and logs. */
    String name();

    static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
