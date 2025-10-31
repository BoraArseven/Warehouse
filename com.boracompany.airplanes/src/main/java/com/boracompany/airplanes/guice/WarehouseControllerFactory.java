package com.boracompany.airplanes.guice;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.view.AirplaneView;

public interface WarehouseControllerFactory {
    WarehouseController create(AirplaneView view);
}
