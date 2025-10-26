package com.boracompany.airplanes.view;

import java.util.List;

import com.boracompany.airplanes.model.Airplane;

public interface AirplaneView {

    void showAllAirplanes(List<Airplane> airplanes);

    void showError(String message, Airplane airplane);

    void airplaneAdded(Airplane airplane);

    void airplaneRemoved(Airplane airplane);

    void showErrorAirplaneNotFound(String message, Airplane airplane);
}
