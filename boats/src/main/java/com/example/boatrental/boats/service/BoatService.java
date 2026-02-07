package com.example.boatrental.boats.service;

import com.example.boatrental.boats.contract.BoatLookup;
import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.entity.BoatStatus;
import com.example.boatrental.boats.repository.BoatJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BoatService  implements BoatLookup {

    private final BoatJpaRepository repository;

    public BoatService(BoatJpaRepository repository) {
        this.repository = repository;
    }

    public Boat create(String name) {
        Boat boat = new Boat(BoatId.newId(), name);
        return repository.save(boat);
    }

    public List<Boat> findAll() {
        return repository.findAll();
    }

    public Boat findById(BoatId id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Boat not found: " + id.value())
                );
    }

    public void delete(BoatId id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isAvailableForRental(UUID boatId) {
        return repository.existsByIdAndStatus(new BoatId(boatId), BoatStatus.AVAILABLE);
    }
}
