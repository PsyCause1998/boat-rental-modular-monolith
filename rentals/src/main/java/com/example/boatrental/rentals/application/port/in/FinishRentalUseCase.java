package com.example.boatrental.rentals.application.port.in;

import com.example.boatrental.rentals.application.dto.FinishRentalCommand;

public interface FinishRentalUseCase {
    void finish(FinishRentalCommand command);
}
