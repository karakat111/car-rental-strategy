package com.carrental.strategy.common;

/**
 * Immutable vehicle value object used by pricing and deposit strategies.
 */
public final class Car {
    private final String id;
    private final String model;
    private final String category; // ECONOMY, COMPACT, SUV, LUXURY
    private final double dailyRate;

    public Car(String id, String model, String category, double dailyRate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Car id is required");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Car category is required");
        }
        if (dailyRate < 0) {
            throw new IllegalArgumentException("Daily rate must not be negative");
        }
        this.id = id;
        this.model = model;
        this.category = category;
        this.dailyRate = dailyRate;
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public double getDailyRate() { return dailyRate; }

    @Override
    public String toString() {
        return id + " (" + model + ", " + category + ", $" + dailyRate + "/day)";
    }
}
