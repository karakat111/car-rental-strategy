package com.carrental.strategy;

import com.carrental.strategy.common.Car;
import com.carrental.strategy.common.CustomerType;
import com.carrental.strategy.common.RentalRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalRequestTest {

    private final Car car = new Car("C1", "Corolla", "ECONOMY", 40.0);
    private final LocalDate saturday = LocalDate.of(2026, 10, 10);

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new RentalRequest(null, 1, CustomerType.REGULAR, saturday, "U"));
        assertThrows(IllegalArgumentException.class, () -> new RentalRequest(car, 0, CustomerType.REGULAR, saturday, "U"));
        assertThrows(IllegalArgumentException.class, () -> new RentalRequest(car, 1, null, saturday, "U"));
        assertThrows(IllegalArgumentException.class, () -> new RentalRequest(car, 1, CustomerType.REGULAR, null, "U"));
        assertThrows(IllegalArgumentException.class, () -> new Car("", "X", "ECONOMY", 10.0));
        assertThrows(IllegalArgumentException.class, () -> new Car("C", "X", "ECONOMY", -1.0));
    }

    @Test
    void knowsWeekendAndLongTerm() {
        assertTrue(new RentalRequest(car, 7, CustomerType.REGULAR, saturday, "U").startsOnWeekend());
        assertTrue(new RentalRequest(car, 7, CustomerType.REGULAR, saturday, "U").isLongTerm());
        assertFalse(new RentalRequest(car, 6, CustomerType.REGULAR, saturday.plusDays(2), "U").startsOnWeekend());
        assertFalse(new RentalRequest(car, 6, CustomerType.REGULAR, saturday.plusDays(2), "U").isLongTerm());
    }
}
