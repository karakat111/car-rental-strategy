package com.carrental.strategy.deposit;

import com.carrental.strategy.common.RentalRequest;

import java.util.Map;

/**
 * Flat deposit amount from a table by vehicle category.
 * Ignores the rental price and the number of days.
 */
public class CategoryFlatDepositStrategy implements DepositStrategy {

    private static final Map<String, Double> AMOUNTS = Map.of(
            "ECONOMY", 100.0,
            "COMPACT", 150.0,
            "SUV", 300.0,
            "LUXURY", 500.0);
    private static final double OTHER_CATEGORY_AMOUNT = 200.0;

    @Override
    public double calculateDeposit(RentalRequest request, double rentalPrice) {
        String category = request.getCar().getCategory().toUpperCase();
        return AMOUNTS.getOrDefault(category, OTHER_CATEGORY_AMOUNT);
    }

    @Override
    public String name() {
        return "Flat by category (100-500)";
    }
}
