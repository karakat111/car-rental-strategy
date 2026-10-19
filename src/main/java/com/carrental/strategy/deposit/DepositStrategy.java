package com.carrental.strategy.deposit;

import com.carrental.strategy.common.RentalRequest;

/**
 * Strategy interface - family 2: how to calculate the security deposit.
 */
public interface DepositStrategy {

    /**
     * @param request     rental details
     * @param rentalPrice already calculated total price (some strategies use it)
     * @return deposit amount (never negative)
     */
    double calculateDeposit(RentalRequest request, double rentalPrice);

    String name();

    static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
