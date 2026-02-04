package com.example.boatrentals.customers.repository;

import com.example.boatrentals.customers.entity.Customer;
import com.example.boatrentals.customers.entity.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, CustomerId> {
    boolean existsByEmail(String email);
}

