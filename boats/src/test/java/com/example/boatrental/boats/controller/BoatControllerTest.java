package com.example.boatrental.boats.controller;

import com.example.boatrental.boats.dto.BoatRequest;
import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.service.BoatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoatControllerTest {

    @Mock
    private BoatService service;

    @InjectMocks
    private BoatController controller;

    @Test
    public void should_create_boat() {
        when(service.create("Black Pearl"))
                .thenReturn(new Boat(BoatId.newId(), "Black Pearl"));

        BoatRequest request = new BoatRequest("Black Pearl");
        Boat result = controller.create(request);

        verify(service).create("Black Pearl");
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Black Pearl");
    }

    @Test
    public void should_find_all_boats() {
        controller.findAll();

        verify(service).findAll();
    }

    @Test
    public void should_find_boat_by_id() {
        var id = java.util.UUID.randomUUID();
        controller.findById(id);

        verify(service).findById(new com.example.boatrental.boats.entity.BoatId(id));
    }

    @Test
    public void should_delete_boat() {
        var id = java.util.UUID.randomUUID();
        controller.delete(id);

        verify(service).delete(new BoatId(id));
    }
}
