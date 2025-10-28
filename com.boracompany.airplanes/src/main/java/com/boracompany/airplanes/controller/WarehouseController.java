package com.boracompany.airplanes.controller;

import com.boracompany.airplanes.repository.AirplaneRepository;
import com.boracompany.airplanes.view.AirplaneView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class WarehouseController {

    private AirplaneView airplaneView;
    private AirplaneRepository airplaneRepository;

    @Inject
    public WarehouseController(@Assisted AirplaneView airplaneView, AirplaneRepository airplaneRepository) {
        this.airplaneView = airplaneView;
        this.airplaneRepository = airplaneRepository;
    }

    public void allAirplanes() {
        airplaneView.showAllAirplanes(airplaneRepository.findAll());
    }

}
