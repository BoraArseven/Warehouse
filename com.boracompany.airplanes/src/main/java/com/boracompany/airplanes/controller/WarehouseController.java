package com.boracompany.airplanes.controller;

import com.boracompany.airplanes.model.Airplane;
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

    public void newAirplane(Airplane airplane) {
        Airplane existingAirplane = airplaneRepository.findById(airplane.getId());
        if (existingAirplane != null) {
            airplaneView.showError("Already existing airplane with id " + airplane.getId(), existingAirplane);
            return;
        }
        airplaneRepository.save(airplane);
        airplaneView.airplaneAdded(airplane);
    }

    public void deleteAirplane(Airplane airplanetoDelete) {
        if (airplaneRepository.findById(airplanetoDelete.getId()) == null) {

            airplaneView.showErrorAirplaneNotFound("No existing airplane with id " + airplanetoDelete.getId(),
                    airplanetoDelete);
            return;
        }
        Airplane selectedAirplane = airplaneRepository.findById(airplanetoDelete.getId());
        airplaneRepository.delete(selectedAirplane.getId());
        airplaneView.airplaneRemoved(selectedAirplane);
    }

}
