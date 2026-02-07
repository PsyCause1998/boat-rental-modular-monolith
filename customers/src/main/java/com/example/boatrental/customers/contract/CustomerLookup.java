package com.example.boatrental.customers.contract;

import java.util.UUID;

public interface CustomerLookup {
    boolean exists(UUID id);
}

