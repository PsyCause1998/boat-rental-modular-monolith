package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.FinishRentalCommand;
import com.example.boatrental.rentals.application.exception.RentalNotFoundException;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.application.service.FinishRentalService;
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
class FinishRentalServiceTest {

    @Mock
    RentalRepository rentalRepository;

    @Mock
    TimeProvider timeProvider;

    @InjectMocks
    FinishRentalService service;

    @Test
    void should_finish_rental_when_active() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        RentalId rentalId = new RentalId(rentalUuid);

        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant finishedAt = Instant.parse("2026-01-01T11:00:00Z");

        Rental rental = Rental.create(
                rentalId,
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                createdAt
        );
        rental.start(createdAt); // ACTIVE

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(timeProvider.now()).thenReturn(finishedAt);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.finish(new FinishRentalCommand(rentalUuid));

        // then
        assertThat(rental.status().name()).isEqualTo("FINISHED");

        verify(rentalRepository).findById(rentalId);
        verify(timeProvider).now();
        verify(rentalRepository).save(rental);

        verifyNoMoreInteractions(rentalRepository, timeProvider);
    }

    @Test
    void should_throw_when_rental_not_found() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        RentalId rentalId = new RentalId(rentalUuid);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.finish(new FinishRentalCommand(rentalUuid)))
                .isInstanceOf(RentalNotFoundException.class)
                .hasMessageContaining(rentalUuid.toString());

        verify(rentalRepository).findById(rentalId);
        verifyNoInteractions(timeProvider);
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    void should_propagate_domain_exception_when_rental_not_active() {
        // given
        UUID rentalUuid = UUID.randomUUID();
        RentalId rentalId = new RentalId(rentalUuid);

        // Rental in CREATED state (not started)
        Rental rental = Rental.create(
                rentalId,
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(timeProvider.now()).thenReturn(Instant.parse("2026-01-01T11:00:00Z"));

        // when / then
        assertThatThrownBy(() -> service.finish(new FinishRentalCommand(rentalUuid)))
                .isInstanceOf(InvalidRentalStateException.class);

        verify(rentalRepository).findById(rentalId);
        verify(timeProvider).now();
        verify(rentalRepository, never()).save(any());
        verifyNoMoreInteractions(rentalRepository, timeProvider);
    }
}
