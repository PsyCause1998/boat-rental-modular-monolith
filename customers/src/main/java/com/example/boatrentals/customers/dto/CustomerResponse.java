package com.example.boatrentals.customers.dto;

import com.example.boatrentals.customers.entity.Customer;
import com.example.boatrentals.customers.entity.CustomerStatus;

import java.util.UUID;

public record CustomerResponse(UUID id, String fullName, String email, CustomerStatus status) {

    public static CustomerResponse from(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerResponse(
                customer.id().value(),
                customer.fullName(),
                customer.email(),
                customer.status()
        );
    }
}
