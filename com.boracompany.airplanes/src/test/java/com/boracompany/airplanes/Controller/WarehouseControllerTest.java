package com.boracompany.airplanes.Controller;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.model.Airplane;
import com.boracompany.airplanes.repository.AirplaneRepository;
import com.boracompany.airplanes.view.AirplaneView;

class WarehouseControllerTest {

    @Mock
    private AirplaneRepository airplaneRepository;
    @Mock
    private AirplaneView airplaneView;

    @InjectMocks
    private WarehouseController warehouseController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testAllAirplanes() {
        List<Airplane> airplanes = Arrays.asList(new Airplane());
        when(airplaneRepository.findAll()).thenReturn(airplanes);
        warehouseController.allAirplanes();
        verify(airplaneView).showAllAirplanes(airplanes);
    }

    @Test
    void testNewAirplaneWhenItIsUnique() {
        Airplane airplane = new Airplane("1", "test");
        when(airplaneRepository.findById("1")).thenReturn(null);
        warehouseController.newAirplane(airplane);
        InOrder inOrder = inOrder(airplaneRepository, airplaneView);
        inOrder.verify(airplaneRepository).save(airplane);

        inOrder.verify(airplaneView).airplaneAdded(airplane);
        verifyNoMoreInteractions(ignoreStubs(airplaneRepository));
    }

    @Test
    void testNewAirplaneDuplicateAddition() {
        Airplane newAirplane = new Airplane("1", "test");
        Airplane existingAirplane = new Airplane("1", "airplane");
        when(airplaneRepository.findById("1")).thenReturn(existingAirplane);
        warehouseController.newAirplane(newAirplane);
        verify(airplaneView).showError("Already existing airplane with id 1", existingAirplane);
        verifyNoMoreInteractions(ignoreStubs(airplaneRepository));
    }

    @Test
    void testDeleteAirplaneWhenAirplaneExists() {
        Airplane airplanetoDelete = new Airplane("1", "test");
        when(airplaneRepository.findById("1")).thenReturn(airplanetoDelete);
        warehouseController.deleteAirplane(airplanetoDelete);
        InOrder inOrder = inOrder(airplaneRepository, airplaneView);
        inOrder.verify(airplaneRepository).delete("1");
        inOrder.verify(airplaneView).airplaneRemoved(airplanetoDelete);
    }

    @Test
    void testDeleteAirplaneWhenAirplaneDoesnotExist() {
        Airplane airplanetoDelete = new Airplane("1", "test");
        when(airplaneRepository.findById("1")).thenReturn(null);
        warehouseController.deleteAirplane(airplanetoDelete);
        verify(airplaneView).showErrorAirplaneNotFound("No existing airplane with id 1", airplanetoDelete);
    }

    @Test
    void testDeleteAirplaneWhenAirplaneDoesnotExistdiffId() {
        Airplane airplanetoDelete = new Airplane("2", "test");
        when(airplaneRepository.findById("2")).thenReturn(null);
        warehouseController.deleteAirplane(airplanetoDelete);
        verify(airplaneView).showErrorAirplaneNotFound("No existing airplane with id 2", airplanetoDelete);
    }

}
