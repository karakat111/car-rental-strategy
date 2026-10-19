package com.carrental.strategy.deposit;

import com.carrental.strategy.common.RentalRequest;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Deposit depends on an external credit score.
 * High score gives a lower deposit factor, low or unknown score gives a higher one.
 * The score is obtained only through CreditScoreProvider (isolation from change).
 */
public class RiskBasedDepositStrategy implements DepositStrategy {

    /** Lowest score of each band -> share of the price. Unknown scores fall into the first band. */
    private static final TreeMap<Integer, Double> FACTORS = new TreeMap<>(Map.of(
            0, 0.50,
            550, 0.35,
            650, 0.25,
            750, 0.15));

    private static final Map<String, Double> CATEGORY_FLOOR = Map.of(
            "ECONOMY", 50.0,
            "COMPACT", 75.0,
            "SUV", 150.0,
            "LUXURY", 250.0);
    private static final double OTHER_CATEGORY_FLOOR = 100.0;

    private final CreditScoreProvider creditScoreProvider;

    public RiskBasedDepositStrategy(CreditScoreProvider creditScoreProvider) {
        this.creditScoreProvider = Objects.requireNonNull(creditScoreProvider, "creditScoreProvider");
    }

    @Override
    public double calculateDeposit(RentalRequest request, double rentalPrice) {
        int score = Math.max(0, creditScoreProvider.getScore(request.getCustomerId()));
        double factor = FACTORS.floorEntry(score).getValue();
        String category = request.getCar().getCategory().toUpperCase();
        double floor = CATEGORY_FLOOR.getOrDefault(category, OTHER_CATEGORY_FLOOR);
        return DepositStrategy.round(Math.max(rentalPrice * factor, floor));
    }

    @Override
    public String name() {
        return "Risk-based (credit score x price, with floor)";
    }
}
