package com.carrental.strategy.deposit;

/**
 * Abstraction over an external credit-score data source.
 * Hides the real provider so strategies stay isolated from change
 * (Clean Code Ch. 10 / lecture slide 43).
 */
public interface CreditScoreProvider {

    /**
     * @return score in range 300-850, or -1 if unknown
     */
    int getScore(String customerId);
}
