package com.example.boatrental.rentals.application.port.out;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}
