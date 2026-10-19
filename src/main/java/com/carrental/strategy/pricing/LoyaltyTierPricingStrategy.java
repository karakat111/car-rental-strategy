package com.carrental.strategy.pricing;

import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;

import java.util.Map;

/**
 * Discount comes from a table: customer type x car category.
 * Corporate gets 15% on every category, loyalty gets 10% on ECONOMY and COMPACT only.
 */
public class LoyaltyTierPricingStrategy implements PricingStrategy {

    private static final Map<CustomerType, Map<String, Double>> DISCOUNTS = Map.of(
            CustomerType.CORPORATE, Map.of(
                    "ECONOMY", 0.15, "COMPACT", 0.15, "SUV", 0.15, "LUXURY", 0.15),
            CustomerType.LOYALTY, Map.of(
                    "ECONOMY", 0.10, "COMPACT", 0.10));

    @Override
    public double calculatePrice(RentalRequest request) {
        double base = request.getCar().getDailyRate() * request.getRentalDays();
        String category = request.getCar().getCategory().toUpperCase();
        double discount = DISCOUNTS
                .getOrDefault(request.getCustomerType(), Map.of())
                .getOrDefault(category, 0.0);
        return PricingStrategy.round(base * (1.0 - discount));
    }

    @Override
    public String name() {
        return "Loyalty tier (corporate 15% / loyalty 10% on economy)";
    }
}
