package com.carrental.strategy.pricing;

import com.carrental.strategy.common.RentalRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

/**
 * Charges a higher rate for every calendar day that falls on Saturday or Sunday.
 * Weekday days stay at base rate. Logic is day-by-day, not a global percentage.
 */
public class WeekendPremiumPricingStrategy implements PricingStrategy {

    private static final Map<DayOfWeek, Double> MULTIPLIERS = Map.of(
            DayOfWeek.SATURDAY, 1.35,
            DayOfWeek.SUNDAY, 1.35);
    private static final double WEEKDAY_MULTIPLIER = 1.0;

    @Override
    public double calculatePrice(RentalRequest request) {
        double rate = request.getCar().getDailyRate();
        LocalDate first = request.getStartDate();
        double total = first.datesUntil(first.plusDays(request.getRentalDays()))
                .mapToDouble(day -> rate * MULTIPLIERS.getOrDefault(day.getDayOfWeek(), WEEKDAY_MULTIPLIER))
                .sum();
        return PricingStrategy.round(total);
    }

    @Override
    public String name() {
        return "Weekend premium (Sat/Sun x1.35)";
    }
}
