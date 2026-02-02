package com.example.boatrental.boats.repository;

import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoatJpaRepository extends JpaRepository<Boat, BoatId> {
}
