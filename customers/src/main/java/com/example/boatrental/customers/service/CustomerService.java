package com.example.boatrental.customers.service;

import com.example.boatrental.customers.contract.CustomerLookup;
import com.example.boatrental.customers.entity.CustomerId;
import com.example.boatrental.customers.repository.CustomerJpaRepository;
import com.example.boatrental.customers.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService implements CustomerLookup {

    private final CustomerJpaRepository repository;

    public CustomerService(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    public Customer create(String fullName, String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already used: " + email);
        }
        Customer customer = new Customer(CustomerId.newId(), fullName, email);
        return repository.save(customer);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer findById(CustomerId id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id.value()));
    }

    public void delete(CustomerId id) {
        repository.deleteById(id);
    }

    @Override
    public boolean exists(UUID customerId) {
        return repository.existsById(new CustomerId(customerId));
    }
}