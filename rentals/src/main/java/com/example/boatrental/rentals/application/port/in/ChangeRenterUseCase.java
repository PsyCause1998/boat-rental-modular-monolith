package com.example.boatrental.rentals.application.port.in;

import com.example.boatrental.rentals.application.dto.ChangeRenterCommand;

public interface ChangeRenterUseCase {
    void changeRenter(ChangeRenterCommand command);
}
