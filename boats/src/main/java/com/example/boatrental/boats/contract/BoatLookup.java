package com.example.boatrental.boats.contract;

import java.util.UUID;

public interface BoatLookup {

    public boolean isAvailableForRental(UUID boatId);
}
