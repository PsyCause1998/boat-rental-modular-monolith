package com.example.boatrental.customers.service;

import com.example.boatrental.customers.repository.CustomerJpaRepository;
import com.example.boatrental.customers.entity.Customer;
import com.example.boatrental.customers.entity.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerJpaRepository repository;

    @InjectMocks
    private CustomerService service;


    @Test
    void should_not_create_customer_when_email_already_used() {
        when(repository.existsByEmail("john.doe@email.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.create("John Doe", "john.doe@email.com")
        );

        verify(repository).existsByEmail("john.doe@email.com");
        verify(repository, never()).save(any(Customer.class));
    }


    @Test
    public void should_create_customer() {
        when(repository.existsByEmail("john.doe@email.com")).thenReturn(false);
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer newCustomer = service.create("john Doe", "john.doe@email.com");

        verify(repository).existsByEmail("john.doe@email.com");
        verify(repository).save(newCustomer);
        assertThat(newCustomer).isNotNull();
        assertThat(newCustomer.id()).isNotNull();
        assertThat(newCustomer.fullName()).isEqualTo("john Doe");
        assertThat(newCustomer.email()).isEqualTo("john.doe@email.com");
    }

    @Test
    public void findAll_should_return_empty_list_when_no_customers() {
        var customers = service.findAll();

        verify(repository).findAll();
        assertThat(customers).isNotNull();
        assertThat(customers).isEmpty();
    }

    @Test
    public void findAll_should_list_of_two_Customer() {
        when(repository.findAll()).thenReturn(List.of(new Customer(CustomerId.newId(), "", ""), new Customer(CustomerId.newId(), "", "")));

        List<Customer> customers = service.findAll();

        verify(repository).findAll();
        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(2);
    }

    @Test
    public void findById_should_throw_exception_when_customer_not_found() {
        var id = CustomerId.newId();

        assertThrows(IllegalArgumentException.class, () ->
                service.findById(id)
        );

        verify(repository).findById(id);
    }

    @Test
    public void findById_should_return_Customer() {
        var id = CustomerId.newId();
        Customer customer = new Customer(id, "", "");

        when(repository.findById(id)).thenReturn(Optional.of(customer));

        Customer result = service.findById(id);

        verify(repository).findById(id);
        assertThat(result).isEqualTo(customer);
    }

    @Test
    public void should_delete_customer() {
        var id = CustomerId.newId();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}
