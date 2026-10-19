package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

/**
 * Package pricing: first 7 days at full rate, each further day at 60% of the daily rate.
 * Different structure from a simple overall discount.
 */
public class LongTermPackagePricingStrategy implements PricingStrategy {

    private static final int FULL_RATE_DAYS = 7;
    private static final double EXTRA_DAY_FACTOR = 0.60;

    @Override
    public double calculatePrice(RentalRequest request) {
        int days = request.getRentalDays();
        int fullDays = Math.min(days, FULL_RATE_DAYS);
        int extraDays = Math.max(0, days - FULL_RATE_DAYS);
        double rate = request.getCar().getDailyRate();
        return PricingStrategy.round(rate * (fullDays + EXTRA_DAY_FACTOR * extraDays));
    }

    @Override
    public String name() {
        return "Long-term package (7 full + 60% extra days)";
    }
}
