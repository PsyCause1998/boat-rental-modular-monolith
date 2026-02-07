package com.example.boatrental.config;

import com.example.boatrental.boats.contract.BoatLookup;
import com.example.boatrental.customers.contract.CustomerLookup;
import com.example.boatrental.rentals.application.port.in.ChangeRenterUseCase;
import com.example.boatrental.rentals.application.port.in.FinishRentalUseCase;
import com.example.boatrental.rentals.application.port.in.StartRentalUseCase;
import com.example.boatrental.rentals.application.port.out.BoatAvailabilityPort;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.application.service.ChangeRenterService;
import com.example.boatrental.rentals.application.service.FinishRentalService;
import com.example.boatrental.rentals.application.service.StartRentalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RentalsWiringConfig {

    @Bean
    CustomerExistencePort customerExistencePort(
            CustomerLookup customerLookup
    ) {
        return customerLookup::exists;
    }

    @Bean
    BoatAvailabilityPort boatAvailabilityPort(
            BoatLookup boatLookup
    ) {
        return boatLookup::isAvailableForRental;
    }

    @Bean
    StartRentalUseCase startRentalUseCase(
            RentalRepository rentalRepository,
            BoatAvailabilityPort boatAvailabilityPort,
            CustomerExistencePort customerExistencePort,
            TimeProvider timeProvider
    ) {
        return new StartRentalService(
                rentalRepository,
                boatAvailabilityPort,
                customerExistencePort,
                timeProvider
        );
    }

    @Bean
    ChangeRenterUseCase changeRenterUseCase(
            RentalRepository rentalRepository,
            TimeProvider timeProvider,
            CustomerExistencePort customerExistencePort
    ) {
        return new ChangeRenterService(
                rentalRepository,
                timeProvider,
                customerExistencePort
        );
    }

    @Bean
    FinishRentalUseCase finishRentalUseCase(
            RentalRepository rentalRepository,
            TimeProvider timeProvider
    ) {
        return new FinishRentalService(
                rentalRepository,
                timeProvider
        );
    }
}
