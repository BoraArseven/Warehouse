package com.boracompany.airplanes.controller;

import com.boracompany.airplanes.repository.AirplaneRepository;
import com.boracompany.airplanes.view.swing.AirplaneSwingView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class WarehouseController {
    private AirplaneSwingView airplaneView;
    private AirplaneRepository airplaneRepository;

    @Inject
    public WarehouseController(@Assisted AirplaneSwingView airplaneView, AirplaneRepository airplaneRepository) {
        this.airplaneView = airplaneView;
        this.airplaneRepository = airplaneRepository;
    }

    public void allAirplanes() {
        airplaneView.showAllAirplanes(airplaneRepository.findAll());
    }

}
