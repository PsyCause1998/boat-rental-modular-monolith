package com.example.boatrental.boats.contract;

import java.util.UUID;

public interface BoatLookup {

    boolean isAvailableForRental(UUID boatId);
}
