package com.boracompany.airplanes.guice;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.view.swing.AirplaneSwingView;

public interface WarehouseControllerFactory {
    WarehouseController create(AirplaneSwingView view);
}
