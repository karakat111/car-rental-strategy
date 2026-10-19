package com.carrental.strategy.pricing;

import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * BEFORE refactoring: all pricing variants live in one class and are chosen by a switch.
 * Adding a variant means editing this class (violates Open/Closed).
 */
public class PricingCalculator {

    private PricingMode mode = PricingMode.STANDARD;

    public void setMode(PricingMode mode) {
        this.mode = mode;
    }

    public double calculate(RentalRequest request) {
        return switch (mode) {
            case STANDARD -> round(request.getCar().getDailyRate() * request.getRentalDays());
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
        return round(total);
    }

    private double longTerm(RentalRequest request) {
        int days = request.getRentalDays();
        double rate = request.getCar().getDailyRate();
        if (days <= 7) {
            return round(rate * days);
        }
        return round(rate * 7 + rate * 0.6 * (days - 7));
    }

    private double loyalty(RentalRequest request) {
        double base = request.getCar().getDailyRate() * request.getRentalDays();
        String category = request.getCar().getCategory().toUpperCase();
        if (request.getCustomerType() == CustomerType.CORPORATE) {
            return round(base * 0.85);
        }
        if (request.getCustomerType() == CustomerType.LOYALTY
                && (category.equals("ECONOMY") || category.equals("COMPACT"))) {
            return round(base * 0.90);
        }
        return round(base);
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
