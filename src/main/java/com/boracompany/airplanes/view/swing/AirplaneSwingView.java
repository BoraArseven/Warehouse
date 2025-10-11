package com.boracompany.airplanes.view.swing;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.model.Airplane;
import com.boracompany.airplanes.view.AirplaneView;

public class AirplaneSwingView extends JFrame implements AirplaneView {

    public WarehouseController getWarehouseController() {
        return warehouseController;
    }

    public void setWarehouseController(WarehouseController warehouseController) {
        this.warehouseController = warehouseController;
    }

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private WarehouseController warehouseController;

    public void start() {
        setVisible(true);
        getWarehouseController().allAirplanes();
    }

    /**
     * Create the frame.
     */
    public AirplaneSwingView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

    }

    @Override
    public void showError(String message, Airplane airplane) {
        // TODO Auto-generated method stub

    }

    @Override
    public void airplaneAdded(Airplane airplane) {
        // TODO Auto-generated method stub

    }

    @Override
    public void airplaneRemoved(Airplane airplane) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showErrorAirplaneNotFound(String message, Airplane airplane) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showAllAirplanes(List<Airplane> airplanes) {
        // TODO Auto-generated method stub

    }

}
