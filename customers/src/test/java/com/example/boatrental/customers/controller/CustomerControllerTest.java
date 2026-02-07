package com.example.boatrental.customers.controller;

import com.example.boatrental.customers.dto.CustomerRequest;
import com.example.boatrental.customers.dto.CustomerResponse;
import com.example.boatrental.customers.entity.Customer;
import com.example.boatrental.customers.entity.CustomerId;
import com.example.boatrental.customers.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController controller;

    @Test
    public void should_create_customer() {
        when(service.create("John Doe", "John.doe@email.com")).thenReturn(new Customer(CustomerId.newId(), "John Doe", "John.doe@email.com"));

        CustomerRequest request = new CustomerRequest("John Doe", "John.doe@email.com");

        CustomerResponse response = controller.create(request);

        verify(service).create("John Doe", "John.doe@email.com");
        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("John.doe@email.com");
    }

    @Test
    public void should_find_all_customers() {
        controller.findAll();

        verify(service).findAll();
    }

    @Test
    public void should_find_customer_by_id() {
        var id = java.util.UUID.randomUUID();
        controller.findById(id);

        verify(service).findById(new CustomerId(id));
    }

    @Test
    public void should_delete_customer() {
        var id = java.util.UUID.randomUUID();
        controller.delete(id);

        verify(service).delete(new CustomerId(id));
    }
}
