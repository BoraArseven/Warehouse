package com.boracompany.airplanes.Controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testAllAirplanes() {
        List<Airplane> airplanes = Arrays.asList(new Airplane());
        when(airplaneRepository.findAll()).thenReturn(airplanes);
        warehouseController.allAirplanes();
        verify(airplaneView).showAllAirplanes(airplanes);
    }

}
