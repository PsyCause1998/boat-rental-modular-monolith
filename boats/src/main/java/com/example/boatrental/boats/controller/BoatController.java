package com.example.boatrental.boats.controller;

import com.example.boatrental.boats.dto.BoatRequest;
import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.service.BoatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boats")
public class BoatController {

    private final BoatService service;

    public BoatController(BoatService service) {
        this.service = service;
    }

    @PostMapping
    public Boat create(@RequestBody BoatRequest request) {
        return service.create(request.name());
    }

    @GetMapping
    public List<Boat> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Boat findById(@PathVariable UUID id) {
        return service.findById(new BoatId(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(new BoatId(id));
    }
}
