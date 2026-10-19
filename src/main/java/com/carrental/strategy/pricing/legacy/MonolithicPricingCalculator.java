package com.carrental.strategy.pricing.legacy;

import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * BAD design (pre-refactor): all pricing variants in one class with switch/if-else.
 * Kept for Git history demonstration only. Prefer PricingStrategy hierarchy.
 *
 * Commit message reference: "before: monolithic pricing with switch"
 */
public class MonolithicPricingCalculator {

    public enum Mode { STANDARD, WEEKEND_PREMIUM, LONG_TERM, LOYALTY_TIER }

    private Mode mode = Mode.STANDARD;

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public double calculate(RentalRequest request) {
        return switch (mode) {
            case STANDARD -> request.getCar().getDailyRate() * request.getRentalDays();
            case WEEKEND_PREMIUM -> weekend(request);
            case LONG_TERM -> longTerm(request);
            case LOYALTY_TIER -> loyalty(request);
        };
    }

    private double weekend(RentalRequest request) {
        double total = 0;
        LocalDate day = request.getStartDate();
        for (int i = 0; i < request.getRentalDays(); i++) {
            DayOfWeek d = day.getDayOfWeek();
            boolean we = d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
            total += request.getCar().getDailyRate() * (we ? 1.35 : 1.0);
            day = day.plusDays(1);
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private double longTerm(RentalRequest request) {
        int days = request.getRentalDays();
        double rate = request.getCar().getDailyRate();
        if (days <= 7) return Math.round(rate * days * 100.0) / 100.0;
        return Math.round((rate * 7 + rate * 0.6 * (days - 7)) * 100.0) / 100.0;
    }

    private double loyalty(RentalRequest request) {
        double base = request.getCar().getDailyRate() * request.getRentalDays();
        if (request.getCustomerType() == CustomerType.CORPORATE) {
            return Math.round(base * 0.85 * 100.0) / 100.0;
        }
        String cat = request.getCar().getCategory().toUpperCase();
        if (request.getCustomerType() == CustomerType.LOYALTY
                && (cat.equals("ECONOMY") || cat.equals("COMPACT"))) {
            return Math.round(base * 0.90 * 100.0) / 100.0;
        }
        return Math.round(base * 100.0) / 100.0;
    }
}
