package com.carrental.strategy.common;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Input data for pricing and deposit calculations.
 */
public final class RentalRequest {
    private final Car car;
    private final int rentalDays;
    private final CustomerType customerType;
    private final LocalDate startDate;
    private final String customerId;

    public RentalRequest(Car car, int rentalDays, CustomerType customerType,
                         LocalDate startDate, String customerId) {
        if (car == null) {
            throw new IllegalArgumentException("Car is required");
        }
        if (rentalDays < 1) {
            throw new IllegalArgumentException("Rental days must be at least 1");
        }
        if (customerType == null) {
            throw new IllegalArgumentException("Customer type is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        this.car = car;
        this.rentalDays = rentalDays;
        this.customerType = customerType;
        this.startDate = startDate;
        this.customerId = customerId;
    }

    public Car getCar() { return car; }
    public int getRentalDays() { return rentalDays; }
    public CustomerType getCustomerType() { return customerType; }
    public LocalDate getStartDate() { return startDate; }
    public String getCustomerId() { return customerId; }

    public boolean startsOnWeekend() {
        DayOfWeek d = startDate.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    public boolean isLongTerm() {
        return rentalDays >= 7;
    }

    @Override
    public String toString() {
        return "RentalRequest{car=" + car.getId() + ", days=" + rentalDays
                + ", customer=" + customerType + ", start=" + startDate + "}";
    }
}
