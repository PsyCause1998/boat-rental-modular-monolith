package com.example.boatrental.rentals.infrastructure.persistence.adapter;

import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalStatusJpa;
import com.example.boatrental.rentals.infrastructure.persistence.mapper.RentalPersistenceMapper;
import com.example.boatrental.rentals.infrastructure.persistence.repository.RentalSpringDataRepository;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.domain.model.BoatId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RentalRepositoryJpaAdapter implements RentalRepository {

    private final RentalSpringDataRepository repo;

    public RentalRepositoryJpaAdapter(RentalSpringDataRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Rental> findById(RentalId id) {
        return repo.findById(id.value()).map(RentalPersistenceMapper::toDomain);
    }

    @Override
    public Rental save(Rental rental) {
        return RentalPersistenceMapper.toDomain(repo.save(RentalPersistenceMapper.toEntity(rental)));
    }

    @Override
    public boolean existsActiveRentalForBoat(BoatId boat) {
        return repo.existsByBoatIdAndStatus(boat.value(), RentalStatusJpa.ACTIVE);
    }
}
