package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

/**
 * Flat daily rate x days. No modifiers.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(RentalRequest request) {
        return PricingStrategy.round(request.getCar().getDailyRate() * request.getRentalDays());
    }

    @Override
    public String name() {
        return "Standard (daily x days)";
    }
}
