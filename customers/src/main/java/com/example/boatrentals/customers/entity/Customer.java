package com.example.boatrentals.customers.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "customers")
public class Customer {

    @EmbeddedId
    private CustomerId id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    protected Customer() {
        // JPA
    }

    public Customer(CustomerId id, String fullName, String email) {
        this.id = Objects.requireNonNull(id);
        this.fullName = Objects.requireNonNull(fullName);
        this.email = Objects.requireNonNull(email);
        this.status = CustomerStatus.ACTIVE;
    }

    public CustomerId id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public String email() {
        return email;
    }

    public CustomerStatus status() {
        return status;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }
}
