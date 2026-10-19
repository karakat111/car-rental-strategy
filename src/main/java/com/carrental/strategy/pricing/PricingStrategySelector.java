package com.carrental.strategy.pricing;

import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;

/**
 * Chooses a pricing strategy from the current situation.
 * if/switch is allowed only here - selection is this class's job.
 */
public class PricingStrategySelector {

    /**
     * Rules (in priority order):
     * 1. Corporate or Loyalty customer -> LoyaltyTier
     * 2. Rental of 7 days or more      -> LongTermPackage
     * 3. Starts on weekend             -> WeekendPremium
     * 4. Otherwise                     -> Standard
     */
    public PricingStrategy select(RentalRequest request) {
        if (request.getCustomerType() == CustomerType.CORPORATE
                || request.getCustomerType() == CustomerType.LOYALTY) {
            return new LoyaltyTierPricingStrategy();
        }
        if (request.isLongTerm()) {
            return new LongTermPackagePricingStrategy();
        }
        if (request.startsOnWeekend()) {
            return new WeekendPremiumPricingStrategy();
        }
        return new StandardPricingStrategy();
    }
}
