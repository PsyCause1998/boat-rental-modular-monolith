package com.example.boatrental.rentals.infrastructure.api;

import com.example.boatrental.rentals.application.dto.ChangeRenterCommand;
import com.example.boatrental.rentals.application.dto.FinishRentalCommand;
import com.example.boatrental.rentals.application.dto.StartRentalCommand;
import com.example.boatrental.rentals.application.port.in.ChangeRenterUseCase;
import com.example.boatrental.rentals.application.port.in.FinishRentalUseCase;
import com.example.boatrental.rentals.application.port.in.StartRentalUseCase;
import com.example.boatrental.rentals.infrastructure.api.dto.ChangeRenterRequest;
import com.example.boatrental.rentals.infrastructure.api.dto.StartRentalRequest;
import com.example.boatrental.rentals.infrastructure.api.dto.StartRentalResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rentals")
class RentalController {

    private final StartRentalUseCase startRentalUseCase;
    private final ChangeRenterUseCase changeRenterUseCase;
    private final FinishRentalUseCase finishRentalUseCase;

    RentalController(StartRentalUseCase startRentalUseCase,
                     ChangeRenterUseCase changeRenterUseCase,
                     FinishRentalUseCase finishRentalUseCase) {
        this.startRentalUseCase = startRentalUseCase;
        this.changeRenterUseCase = changeRenterUseCase;
        this.finishRentalUseCase = finishRentalUseCase;
    }

    @PostMapping
    ResponseEntity<StartRentalResponse> start(@RequestBody @Valid StartRentalRequest request) {
        var rentalId = startRentalUseCase.start(
                new StartRentalCommand(request.boatId(), request.customerId())
        );
        return ResponseEntity.status(201).body(new StartRentalResponse(rentalId.value()));
    }

    @PostMapping("/{rentalId}/change-renter")
    ResponseEntity<Void> changeRenter(@PathVariable UUID rentalId,
                                      @RequestBody @Valid ChangeRenterRequest request) {
        changeRenterUseCase.changeRenter(new ChangeRenterCommand(rentalId, request.newCustomerId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rentalId}/finish")
    ResponseEntity<Void> finish(@PathVariable UUID rentalId) {
        finishRentalUseCase.finish(new FinishRentalCommand(rentalId));
        return ResponseEntity.noContent().build();
    }
}
