package com.carrental.strategy.deposit;

import com.carrental.strategy.common.RentalRequest;

import java.util.Objects;

/**
 * Context for the deposit strategy family.
 * No if/switch/instanceof on the strategy type.
 */
public class DepositContext {

    private DepositStrategy strategy;

    public DepositContext(DepositStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(DepositStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public DepositStrategy getStrategy() {
        return strategy;
    }

    public double calculateDeposit(RentalRequest request, double rentalPrice) {
        return strategy.calculateDeposit(request, rentalPrice);
    }

    public String currentStrategyName() {
        return strategy.name();
    }
}
