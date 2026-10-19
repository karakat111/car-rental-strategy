package com.carrental.strategy.deposit;

import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;

/**
 * Chooses a deposit strategy from the situation.
 * if/switch allowed only here.
 */
public class DepositStrategySelector {

    private final CreditScoreProvider creditScoreProvider;

    public DepositStrategySelector(CreditScoreProvider creditScoreProvider) {
        this.creditScoreProvider = creditScoreProvider;
    }

    /**
     * Rules (in priority order):
     * 1. Corporate customer -> flat deposit by category (the company carries the risk)
     * 2. Luxury car         -> risk-based (higher exposure)
     * 3. Otherwise          -> 25% of the price
     */
    public DepositStrategy select(RentalRequest request) {
        if (request.getCustomerType() == CustomerType.CORPORATE) {
            return new CategoryFlatDepositStrategy();
        }
        if ("LUXURY".equalsIgnoreCase(request.getCar().getCategory())) {
            return new RiskBasedDepositStrategy(creditScoreProvider);
        }
        return new PercentageOfPriceDepositStrategy();
    }
}
