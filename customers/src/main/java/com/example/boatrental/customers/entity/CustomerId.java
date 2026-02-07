package com.example.boatrental.customers.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record CustomerId(UUID value) implements Serializable {

    public CustomerId {
        if (value == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }
}
