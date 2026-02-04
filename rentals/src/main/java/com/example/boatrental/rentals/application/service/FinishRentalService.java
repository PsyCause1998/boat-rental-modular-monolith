package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.FinishRentalCommand;
import com.example.boatrental.rentals.application.exception.RentalNotFoundException;
import com.example.boatrental.rentals.application.port.in.FinishRentalUseCase;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;

import java.time.Instant;

public class FinishRentalService implements FinishRentalUseCase {

    private final RentalRepository rentalRepository;
    private final TimeProvider timeProvider;

    public FinishRentalService(RentalRepository rentalRepository, TimeProvider timeProvider) {
        this.rentalRepository = rentalRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public void finish(FinishRentalCommand command) {
        // 1) find rental
        Rental rental = rentalRepository.findById(new RentalId(command.rentalId()))
                .orElseThrow(() -> new RentalNotFoundException(command.rentalId()));

        // 2) finish rental
        Instant now = timeProvider.now();
        rental.finish(now);

        // 3) save changes
        rentalRepository.save(rental);
    }
}
