package com.example.boatrentals.customers.service;

import com.example.boatrentals.customers.entity.Customer;
import com.example.boatrentals.customers.entity.CustomerId;
import org.springframework.stereotype.Service;
import com.example.boatrentals.customers.repository.CustomerJpaRepository;

import java.util.List;

@Service
public class CustomerService {

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
}