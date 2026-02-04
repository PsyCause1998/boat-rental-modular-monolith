package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.StartRentalCommand;
import com.example.boatrental.rentals.application.exception.ActiveRentalAlreadyExistsException;
import com.example.boatrental.rentals.application.exception.BoatNotAvailableException;
import com.example.boatrental.rentals.application.exception.CustomerNotFoundException;
import com.example.boatrental.rentals.application.port.out.BoatAvailabilityPort;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.application.service.StartRentalService;
import com.example.boatrental.rentals.domain.model.Rental;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartRentalServiceTest {

    @Mock
    RentalRepository rentalRepository;
    @Mock
    BoatAvailabilityPort boatAvailabilityPort;
    @Mock
    CustomerExistencePort customerExistencePort;
    @Mock
    TimeProvider timeProvider;

    @InjectMocks
    StartRentalService service;

    @Test
    void should_start_rental_when_valid() {
        UUID boatId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(boatAvailabilityPort.isAvailableForRental(any())).thenReturn(true);
        when(rentalRepository.existsActiveRentalForBoat(any())).thenReturn(false);
        when(customerExistencePort.exists(any())).thenReturn(true);
        when(timeProvider.now()).thenReturn(Instant.parse("2026-01-01T10:00:00Z"));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        var rentalId = service.start(new StartRentalCommand(boatId, customerId));

        assertThat(rentalId).isNotNull();

        verify(boatAvailabilityPort).isAvailableForRental(any());
        verify(rentalRepository).existsActiveRentalForBoat(any());
        verify(customerExistencePort).exists(any());
        verify(timeProvider).now();
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void should_throw_when_boat_not_available() {
        UUID boatId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(boatAvailabilityPort.isAvailableForRental(any())).thenReturn(false);

        assertThatThrownBy(() -> service.start(new StartRentalCommand(boatId, customerId)))
                .isInstanceOf(BoatNotAvailableException.class)
                .hasMessageContaining(boatId.toString());

        verify(boatAvailabilityPort).isAvailableForRental(any());
        verifyNoMoreInteractions(rentalRepository, customerExistencePort, timeProvider);
    }

    @Test
    void should_throw_when_active_rental_already_exists_for_boat() {
        UUID boatId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(boatAvailabilityPort.isAvailableForRental(any())).thenReturn(true);
        when(rentalRepository.existsActiveRentalForBoat(any())).thenReturn(true);

        assertThatThrownBy(() -> service.start(new StartRentalCommand(boatId, customerId)))
                .isInstanceOf(ActiveRentalAlreadyExistsException.class)
                .hasMessageContaining(boatId.toString());

        verify(boatAvailabilityPort).isAvailableForRental(any());
        verify(rentalRepository).existsActiveRentalForBoat(any());
        verifyNoMoreInteractions(customerExistencePort, timeProvider);
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void should_throw_when_customer_does_not_exist() {
        UUID boatId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(boatAvailabilityPort.isAvailableForRental(any())).thenReturn(true);
        when(rentalRepository.existsActiveRentalForBoat(any())).thenReturn(false);
        when(customerExistencePort.exists(any())).thenReturn(false);

        assertThatThrownBy(() -> service.start(new StartRentalCommand(boatId, customerId)))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(customerId.toString());

        verify(boatAvailabilityPort).isAvailableForRental(any());
        verify(rentalRepository).existsActiveRentalForBoat(any());
        verify(customerExistencePort).exists(any());
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(timeProvider);
    }
}
