package com.example.boatrental.customers.controller;
import com.example.boatrental.customers.dto.CustomerRequest;
import com.example.boatrental.customers.dto.CustomerResponse;
import com.example.boatrental.customers.entity.Customer;
import com.example.boatrental.customers.entity.CustomerId;
import org.springframework.web.bind.annotation.*;
import com.example.boatrental.customers.service.CustomerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public CustomerResponse create(@RequestBody CustomerRequest request) {
        Customer created = service.create(request.fullName(), request.email());
        return CustomerResponse.from(created);
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return service.findAll().stream().map(CustomerResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable UUID id) {
        Customer customer = service.findById(new CustomerId(id));
        return CustomerResponse.from(customer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(new CustomerId(id));
    }
}

