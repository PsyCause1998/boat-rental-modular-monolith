package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.ChangeRenterCommand;
import com.example.boatrental.rentals.application.exception.CustomerNotFoundException;
import com.example.boatrental.rentals.application.exception.RentalNotFoundException;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.domain.exception.InvalidRentalStateException;
import com.example.boatrental.rentals.domain.model.BoatId;
import com.example.boatrental.rentals.domain.model.CustomerId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRenterServiceTest {

    @Mock RentalRepository rentalRepository;
    @Mock TimeProvider timeProvider;
    @Mock CustomerExistencePort customerExistencePort;

    @InjectMocks ChangeRenterService service;

    @Test
    void should_change_renter_when_valid() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        UUID oldCustomerUuid = UUID.randomUUID();
        UUID newCustomerUuid = UUID.randomUUID();

        RentalId rentalId = new RentalId(rentalUuid);
        CustomerId oldCustomerId = new CustomerId(oldCustomerUuid);
        CustomerId newCustomerId = new CustomerId(newCustomerUuid);

        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant changeAt = Instant.parse("2026-01-01T10:30:00Z");

        Rental rental = Rental.create(
                rentalId,
                new BoatId(UUID.randomUUID()),
                oldCustomerId,
                createdAt
        );
        rental.start(createdAt); // ACTIVE

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(customerExistencePort.exists(newCustomerId)).thenReturn(true);
        when(timeProvider.now()).thenReturn(changeAt);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.changeRenter(new ChangeRenterCommand(rentalUuid, newCustomerUuid));

        // then
        assertThat(rental.customer()).isEqualTo(newCustomerId);

        verify(rentalRepository).findById(rentalId);
        verify(customerExistencePort).exists(newCustomerId);
        verify(timeProvider).now();
        verify(rentalRepository).save(rental);

        verifyNoMoreInteractions(rentalRepository, customerExistencePort, timeProvider);
    }

    @Test
    void should_throw_when_rental_not_found() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        UUID newCustomerUuid = UUID.randomUUID();
        RentalId rentalId = new RentalId(rentalUuid);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.changeRenter(new ChangeRenterCommand(rentalUuid, newCustomerUuid)))
                .isInstanceOf(RentalNotFoundException.class)
                .hasMessageContaining(rentalUuid.toString());

        verify(rentalRepository).findById(rentalId);
        verifyNoInteractions(customerExistencePort, timeProvider);
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    void should_throw_when_customer_does_not_exist() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        UUID oldCustomerUuid = UUID.randomUUID();
        UUID newCustomerUuid = UUID.randomUUID();

        RentalId rentalId = new RentalId(rentalUuid);
        CustomerId oldCustomerId = new CustomerId(oldCustomerUuid);
        CustomerId newCustomerId = new CustomerId(newCustomerUuid);

        Rental rental = Rental.create(
                rentalId,
                new BoatId(UUID.randomUUID()),
                oldCustomerId,
                Instant.parse("2026-01-01T10:00:00Z")
        );
        rental.start(Instant.parse("2026-01-01T10:00:00Z")); // ACTIVE

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(customerExistencePort.exists(newCustomerId)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> service.changeRenter(new ChangeRenterCommand(rentalUuid, newCustomerUuid)))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(newCustomerUuid.toString());

        verify(rentalRepository).findById(rentalId);
        verify(customerExistencePort).exists(newCustomerId);
        verifyNoInteractions(timeProvider);
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(rentalRepository, customerExistencePort);
    }

    @Test
    void should_propagate_domain_exception_when_rental_not_active() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        UUID oldCustomerUuid = UUID.randomUUID();
        UUID newCustomerUuid = UUID.randomUUID();

        RentalId rentalId = new RentalId(rentalUuid);
        CustomerId oldCustomerId = new CustomerId(oldCustomerUuid);
        CustomerId newCustomerId = new CustomerId(newCustomerUuid);

        Rental rental = Rental.create(
                rentalId,
                new BoatId(UUID.randomUUID()),
                oldCustomerId,
                Instant.parse("2026-01-01T10:00:00Z")
        );
        // NOTE: not started => CREATED (not ACTIVE)

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(customerExistencePort.exists(newCustomerId)).thenReturn(true);
        when(timeProvider.now()).thenReturn(Instant.parse("2026-01-01T10:30:00Z"));

        // when / then
        assertThatThrownBy(() -> service.changeRenter(new ChangeRenterCommand(rentalUuid, newCustomerUuid)))
                .isInstanceOf(InvalidRentalStateException.class);

        verify(rentalRepository).findById(rentalId);
        verify(customerExistencePort).exists(newCustomerId);
        verify(timeProvider).now();
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(rentalRepository, customerExistencePort, timeProvider);
    }
}
