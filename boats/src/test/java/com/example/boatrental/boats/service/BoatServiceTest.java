package com.example.boatrental.boats.service;

import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.repository.BoatJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoatServiceTest {

    @Mock
    private BoatJpaRepository repository;

    @InjectMocks
    private BoatService service;

    private Boat boat;

    @BeforeEach
    void setUp() {
        boat = new Boat(BoatId.newId(), "Black Pearl");
    }

    @Test
    void should_create_boat() {
        when(repository.save(any(Boat.class))).thenReturn(boat);

        Boat result = service.create("Black Pearl");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Black Pearl");
        assertThat(result.status()).isEqualTo(boat.status());

        verify(repository).save(any(Boat.class));
    }

    @Test
    void should_find_all_boats() {
        when(repository.findAll()).thenReturn(List.of(boat));

        List<Boat> boats = service.findAll();

        assertThat(boats).hasSize(1);
        assertThat(boats.getFirst().name()).isEqualTo("Black Pearl");

        verify(repository).findAll();
    }

    @Test
    void should_find_boat_by_id() {
        when(repository.findById(boat.id())).thenReturn(Optional.of(boat));

        Boat result = service.findById(boat.id());

        assertThat(result).isEqualTo(boat);

        verify(repository).findById(boat.id());
    }

    @Test
    void should_throw_exception_when_boat_not_found() {
        BoatId id = BoatId.newId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Boat not found");

        verify(repository).findById(id);
    }

    @Test
    void should_delete_boat_by_id() {
        BoatId id = BoatId.newId();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}
