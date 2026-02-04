package com.example.boatrental.rentals.application.port.out;

import com.example.boatrental.rentals.domain.model.BoatId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;

import java.util.Optional;

public interface RentalRepository {

    Optional<Rental> findById(RentalId id);

    Rental save(Rental rental);

    boolean existsActiveRentalForBoat(BoatId boat);
}