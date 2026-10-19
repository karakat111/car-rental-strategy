package com.carrental.strategy.pricing;

import java.util.function.Supplier;

/** Names of the pricing algorithms. Each name knows how to build its strategy. */
public enum PricingMode {
    STANDARD(StandardPricingStrategy::new),
    WEEKEND_PREMIUM(WeekendPremiumPricingStrategy::new),
    LONG_TERM(LongTermPackagePricingStrategy::new),
    LOYALTY_TIER(LoyaltyTierPricingStrategy::new);

    private final Supplier<PricingStrategy> factory;

    PricingMode(Supplier<PricingStrategy> factory) {
        this.factory = factory;
    }

    public PricingStrategy createStrategy() {
        return factory.get();
    }
}
