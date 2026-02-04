package com.example.boatrental.rentals.application.port.in;

import com.example.boatrental.rentals.application.dto.StartRentalCommand;
import com.example.boatrental.rentals.domain.model.RentalId;

public interface StartRentalUseCase {
    RentalId start(StartRentalCommand command);
}