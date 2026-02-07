package com.example.boatrental.boats.repository;

import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.entity.BoatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoatJpaRepository extends JpaRepository<Boat, BoatId> {

    boolean existsByIdAndStatus(BoatId id, BoatStatus status);
}
