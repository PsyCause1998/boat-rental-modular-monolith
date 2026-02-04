package com.example.boatrental.rentals.application.service;

import com.example.boatrental.rentals.application.dto.StartRentalCommand;
import com.example.boatrental.rentals.application.exception.ActiveRentalAlreadyExistsException;
import com.example.boatrental.rentals.application.exception.BoatNotAvailableException;
import com.example.boatrental.rentals.application.exception.CustomerNotFoundException;
import com.example.boatrental.rentals.application.port.in.StartRentalUseCase;
import com.example.boatrental.rentals.application.port.out.BoatAvailabilityPort;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.domain.model.BoatId;
import com.example.boatrental.rentals.domain.model.CustomerId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;

import java.time.Instant;

public class StartRentalService implements StartRentalUseCase {

    private final RentalRepository rentalRepository;
    private final BoatAvailabilityPort boatAvailabilityPort;
    private final CustomerExistencePort customerExistencePort;
    private final TimeProvider timeProvider;

    public StartRentalService(
            RentalRepository rentalRepository,
            BoatAvailabilityPort boatAvailabilityPort,
            CustomerExistencePort customerExistencePort,
            TimeProvider timeProvider
    ) {
        this.rentalRepository = rentalRepository;
        this.boatAvailabilityPort = boatAvailabilityPort;
        this.customerExistencePort = customerExistencePort;
        this.timeProvider = timeProvider;
    }

    @Override
    public RentalId start(StartRentalCommand command) {
        BoatId boat = new BoatId(command.boatId());
        CustomerId customer = new CustomerId(command.customerId());

        // 1) boat must be available
        if (!boatAvailabilityPort.isAvailableForRental(boat)) {
            throw new BoatNotAvailableException(command.boatId());
        }

        // 2) enforce "one ACTIVE rental per boat"
        if (rentalRepository.existsActiveRentalForBoat(boat)) {
            throw new ActiveRentalAlreadyExistsException(command.boatId());
        }

        // 3) customer must exist
        if (!customerExistencePort.exists(customer)) {
            throw new CustomerNotFoundException(command.customerId());
        }

        // 4) create aggregate
        Instant now = timeProvider.now();
        RentalId rentalId = RentalId.newId();
        Rental rental = Rental.create(rentalId, boat, customer, now);
        rental.start(now);

        // 5) persist
        rentalRepository.save(rental);

        return rentalId;
    }
}