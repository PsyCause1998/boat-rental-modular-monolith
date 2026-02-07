package com.example.boatrental.customers.repository;

import com.example.boatrental.customers.entity.Customer;
import com.example.boatrental.customers.entity.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, CustomerId> {
    boolean existsByEmail(String email);
}

