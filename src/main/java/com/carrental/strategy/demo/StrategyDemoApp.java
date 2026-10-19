package com.carrental.strategy.demo;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import com.carrental.strategy.deposit.CategoryFlatDepositStrategy;
import com.carrental.strategy.deposit.CreditScoreProvider;
import com.carrental.strategy.deposit.DepositContext;
import com.carrental.strategy.deposit.DepositStrategy;
import com.carrental.strategy.deposit.DepositStrategySelector;
import com.carrental.strategy.deposit.PercentageOfPriceDepositStrategy;
import com.carrental.strategy.deposit.RiskBasedDepositStrategy;
import com.carrental.strategy.deposit.SimulatedCreditScoreProvider;
import com.carrental.strategy.pricing.PricingContext;
import com.carrental.strategy.pricing.PricingMode;
import com.carrental.strategy.pricing.PricingStrategy;
import com.carrental.strategy.pricing.PricingStrategySelector;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console demo: same input under all strategies, manual choice vs Selector, runtime switch.
 * Every prompt has a default - just press Enter to use it.
 */
public class StrategyDemoApp {

    private static final List<Car> CARS = List.of(
            new Car("CAR-001", "Toyota Corolla", "ECONOMY", 35.0),
            new Car("CAR-002", "Honda Civic", "COMPACT", 42.0),
            new Car("CAR-003", "Ford Explorer", "SUV", 65.0),
            new Car("CAR-004", "BMW 5-Series", "LUXURY", 120.0));

    private final Scanner in = new Scanner(System.in);
    private final CreditScoreProvider scores = new SimulatedCreditScoreProvider();
    private final PricingStrategySelector pricingSelector = new PricingStrategySelector();
    private final DepositStrategySelector depositSelector = new DepositStrategySelector(scores);

    public static void main(String[] args) {
        System.out.println("=== Car Rental - Strategy Pattern Demo (Assignment 5) ===\n");
        new StrategyDemoApp().run();
    }

    private void run() {
        try {
            while (true) {
                printMenu();
                String choice = in.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> allPricing(askRequest());
                        case "2" -> allDeposits(askRequest());
                        case "3" -> pricingManualVsAuto(askRequest());
                        case "4" -> depositManualVsAuto(askRequest());
                        case "5" -> runtimeSwitch(askRequest());
                        case "0" -> {
                            System.out.println("Bye.");
                            return;
                        }
                        default -> System.out.println("Unknown option");
                    }
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (NoSuchElementException endOfInput) {
            System.out.println("Bye.");
        }
    }

    private void printMenu() {
        System.out.print("""
                ----------------------------------------
                 1. Same input -> ALL pricing strategies
                 2. Same input -> ALL deposit strategies
                 3. Pricing: MANUAL choice vs AUTOMATIC (Selector)
                 4. Deposit: MANUAL choice vs AUTOMATIC (Selector)
                 5. Switch strategy at RUNTIME (pricing + deposit)
                 0. Exit
                ----------------------------------------
                Choice: """);
    }

    // ------------------------------------------------------------ input

    private String ask(String prompt, String defaultValue) {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String line = in.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    private RentalRequest askRequest() {
        String carId = ask("Car id (CAR-001..CAR-004)", "CAR-001");
        Car car = CARS.stream()
                .filter(c -> c.getId().equalsIgnoreCase(carId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown car: " + carId));
        int days = Integer.parseInt(ask("Rental days", "5"));
        CustomerType type = CustomerType.valueOf(
                ask("Customer type (REGULAR / LOYALTY / CORPORATE)", "REGULAR").toUpperCase());
        LocalDate start = LocalDate.parse(ask("Start date (yyyy-MM-dd, 2026-10-10 is a Saturday)", "2026-10-10"));
        String customerId = ask("Customer id (drives the simulated credit score)", "CUST-001");
        RentalRequest request = new RentalRequest(car, days, type, start, customerId);
        System.out.println("Request: " + request + ", " + start.getDayOfWeek()
                + ", credit score " + scores.getScore(customerId) + "\n");
        return request;
    }

    private int askNumber(String prompt, int max) {
        int number = Integer.parseInt(ask(prompt, "1"));
        if (number < 1 || number > max) {
            throw new IllegalArgumentException("Choose a number from 1 to " + max);
        }
        return number;
    }

    // ------------------------------------------------------------ family 1: pricing

    private void allPricing(RentalRequest request) {
        for (PricingMode mode : PricingMode.values()) {
            PricingStrategy strategy = mode.createStrategy();
            System.out.printf("  %-52s -> $%8.2f%n", strategy.name(), strategy.calculatePrice(request));
        }
    }

    private void pricingManualVsAuto(RentalRequest request) {
        PricingMode[] modes = PricingMode.values();
        for (int i = 0; i < modes.length; i++) {
            System.out.println("  " + (i + 1) + ". " + modes[i].createStrategy().name());
        }
        PricingMode manualMode = modes[askNumber("Manual choice (number)", modes.length) - 1];

        PricingContext context = new PricingContext(manualMode.createStrategy());
        System.out.printf("MANUAL: %-52s -> $%8.2f%n", context.currentStrategyName(), context.calculatePrice(request));

        context.setStrategy(pricingSelector.select(request));
        System.out.printf("AUTO  : %-52s -> $%8.2f%n", context.currentStrategyName(), context.calculatePrice(request));
    }

    // ------------------------------------------------------------ family 2: deposit

    private List<DepositStrategy> depositStrategies() {
        return List.of(
                new PercentageOfPriceDepositStrategy(),
                new CategoryFlatDepositStrategy(),
                new RiskBasedDepositStrategy(scores));
    }

    private double standardPrice(RentalRequest request) {
        return PricingMode.STANDARD.createStrategy().calculatePrice(request);
    }

    private void allDeposits(RentalRequest request) {
        double price = standardPrice(request);
        System.out.printf("Rental price used as input: $%.2f%n", price);
        for (DepositStrategy strategy : depositStrategies()) {
            System.out.printf("  %-52s -> $%8.2f%n", strategy.name(), strategy.calculateDeposit(request, price));
        }
    }

    private void depositManualVsAuto(RentalRequest request) {
        double price = standardPrice(request);
        List<DepositStrategy> all = depositStrategies();
        for (int i = 0; i < all.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + all.get(i).name());
        }
        DepositStrategy manual = all.get(askNumber("Manual choice (number)", all.size()) - 1);

        DepositContext context = new DepositContext(manual);
        System.out.printf("MANUAL: %-52s -> $%8.2f%n", context.currentStrategyName(),
                context.calculateDeposit(request, price));

        context.setStrategy(depositSelector.select(request));
        System.out.printf("AUTO  : %-52s -> $%8.2f%n", context.currentStrategyName(),
                context.calculateDeposit(request, price));
    }

    // ------------------------------------------------------------ runtime switching

    private void runtimeSwitch(RentalRequest request) {
        PricingContext pricing = new PricingContext(PricingMode.STANDARD.createStrategy());
        System.out.println("Pricing context, one object, strategy replaced while running:");
        for (PricingMode mode : PricingMode.values()) {
            pricing.setStrategy(mode.createStrategy());
            System.out.printf("  setStrategy(%-15s) -> %-52s $%8.2f%n",
                    mode, pricing.currentStrategyName(), pricing.calculatePrice(request));
        }

        double price = standardPrice(request);
        DepositContext deposit = new DepositContext(depositStrategies().get(0));
        System.out.println("Deposit context, one object, strategy replaced while running:");
        for (DepositStrategy strategy : depositStrategies()) {
            deposit.setStrategy(strategy);
            System.out.printf("  setStrategy -> %-52s $%8.2f%n",
                    deposit.currentStrategyName(), deposit.calculateDeposit(request, price));
        }
        System.out.println("(The contexts never branch on the strategy type - only setStrategy + delegate.)");
    }
}
