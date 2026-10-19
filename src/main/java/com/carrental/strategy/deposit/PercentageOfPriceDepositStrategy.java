package com.carrental.strategy.deposit;

import com.carrental.strategy.common.RentalRequest;

/**
 * Deposit = fixed percentage of the rental price (25%).
 */
public class PercentageOfPriceDepositStrategy implements DepositStrategy {

    private static final double PERCENT = 0.25;

    @Override
    public double calculateDeposit(RentalRequest request, double rentalPrice) {
        return DepositStrategy.round(rentalPrice * PERCENT);
    }

    @Override
    public String name() {
        return "25% of rental price";
    }
}
