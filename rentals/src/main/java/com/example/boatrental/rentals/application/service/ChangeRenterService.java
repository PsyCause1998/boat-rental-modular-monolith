package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.ChangeRenterCommand;
import com.example.boatrental.rentals.application.exception.CustomerNotFoundException;
import com.example.boatrental.rentals.application.exception.RentalNotFoundException;
import com.example.boatrental.rentals.application.port.in.ChangeRenterUseCase;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.domain.model.CustomerId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;

import java.time.Instant;

public class ChangeRenterService implements ChangeRenterUseCase {
    private final RentalRepository rentalRepository;
    private final TimeProvider timeProvider;
    private final CustomerExistencePort customerExistencePort;

    public ChangeRenterService(RentalRepository rentalRepository,
                               TimeProvider timeProvider,
                               CustomerExistencePort customerExistencePort) {
        this.rentalRepository = rentalRepository;
        this.timeProvider = timeProvider;
        this.customerExistencePort = customerExistencePort;
    }


    @Override
    public void changeRenter(ChangeRenterCommand command) {
        // 1) find rental
        RentalId rentalId = new RentalId(command.rentalId());
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(command.rentalId()));

        // 2) change renter
        Instant now = timeProvider.now();
        CustomerId newCustomerId = new CustomerId(command.newCustomerId());
        if (customerExistencePort.exists(newCustomerId)) {
            throw new CustomerNotFoundException(command.newCustomerId());
        }
        rental.changeRenter(newCustomerId, now);

        // 3) save changes
        rentalRepository.save(rental);
    }
}
