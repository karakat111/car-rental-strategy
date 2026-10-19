package com.carrental.strategy.deposit;

/**
 * Simulated external credit bureau. Deterministic hash-based scores for demos.
 */
public class SimulatedCreditScoreProvider implements CreditScoreProvider {

    private static final int MIN_SCORE = 300;
    private static final int SCORE_SPAN = 551; // 300..850

    @Override
    public int getScore(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return -1;
        }
        return MIN_SCORE + Math.floorMod(customerId.hashCode(), SCORE_SPAN);
    }
}
