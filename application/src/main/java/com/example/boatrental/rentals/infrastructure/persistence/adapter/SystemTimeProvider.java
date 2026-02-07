package com.example.boatrental.rentals.infrastructure.persistence.adapter;

import com.example.boatrental.rentals.application.port.out.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemTimeProvider implements TimeProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
