package com.boracompany.airplanes.view.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
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
    private transient WarehouseController warehouseController;
    private JTextField txtIdtextfield;
    private JTextField modeltextField;
    private JButton btnAdd;
    private JList<Airplane> list;
    private JScrollPane scrollPane;
    private JButton btnDelete;
    private JLabel label;
    private DefaultListModel<Airplane> listAirplanesModel;

    public void start() {
        setVisible(true);
        getWarehouseController().allAirplanes();
    }

    DefaultListModel<Airplane> getListAirplanesModel() {
        return listAirplanesModel;
    }

    /**
     * Create the frame.
     */
    public AirplaneSwingView() {
        setTitle("Airplane View");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        contentPane.setLayout(gbl_contentPane);

        JLabel lblId = new JLabel("id");
        GridBagConstraints gbc_lblId = new GridBagConstraints();
        gbc_lblId.insets = new Insets(0, 0, 5, 5);
        gbc_lblId.gridx = 0;
        gbc_lblId.gridy = 1;
        contentPane.add(lblId, gbc_lblId);

        txtIdtextfield = new JTextField();

        txtIdtextfield.setName("idTextBox");
        KeyAdapter btnAddEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                btnAdd.setEnabled(
                        !txtIdtextfield.getText().trim().isEmpty() && !modeltextField.getText().trim().isEmpty());
            }
        };
        txtIdtextfield.addKeyListener(btnAddEnabler);
        GridBagConstraints gbc_txtIdtextfield = new GridBagConstraints();
        gbc_txtIdtextfield.insets = new Insets(0, 0, 5, 0);
        gbc_txtIdtextfield.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtIdtextfield.gridx = 1;
        gbc_txtIdtextfield.gridy = 1;
        contentPane.add(txtIdtextfield, gbc_txtIdtextfield);
        txtIdtextfield.setColumns(10);

        JLabel lblModel = new JLabel("model");
        GridBagConstraints gbc_lblModel = new GridBagConstraints();
        gbc_lblModel.anchor = GridBagConstraints.EAST;
        gbc_lblModel.insets = new Insets(0, 0, 5, 5);
        gbc_lblModel.gridx = 0;
        gbc_lblModel.gridy = 2;
        contentPane.add(lblModel, gbc_lblModel);

        modeltextField = new JTextField();
        modeltextField.addKeyListener(btnAddEnabler);
        txtIdtextfield.addKeyListener(btnAddEnabler);
        modeltextField.setName("modelTextBox");
        GridBagConstraints gbc_modeltextField = new GridBagConstraints();
        gbc_modeltextField.insets = new Insets(0, 0, 5, 0);
        gbc_modeltextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_modeltextField.gridx = 1;
        gbc_modeltextField.gridy = 2;
        contentPane.add(modeltextField, gbc_modeltextField);
        modeltextField.setColumns(10);

        btnAdd = new JButton("Add");
        btnAdd.setEnabled(false);
        btnAdd.setName("airplaneAddButton");
        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
        gbc_btnAdd.gridwidth = 2;
        gbc_btnAdd.gridx = 0;
        gbc_btnAdd.gridy = 3;
        contentPane.add(btnAdd, gbc_btnAdd);

        scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 4;
        contentPane.add(scrollPane, gbc_scrollPane);
        listAirplanesModel = new DefaultListModel<>();
        list = new JList<>(listAirplanesModel);
        scrollPane.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setName("airplaneList");
        list.addListSelectionListener(e -> btnDelete.setEnabled(list.getSelectedIndex() != -1));
        list.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Airplane airplane = (Airplane) value;
                return super.getListCellRendererComponent(list, getDisplayString(airplane), index, isSelected,
                        cellHasFocus);
            }
        });
        btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.setName("delete");
        btnDelete.setActionCommand("Delete");
        btnDelete.addActionListener(e -> warehouseController.deleteAirplane(list.getSelectedValue()));

        GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
        gbc_btnDelete.gridx = 1;
        gbc_btnDelete.gridy = 5;
        contentPane.add(btnDelete, gbc_btnDelete);

        label = new JLabel(" ");
        label.setName("errorLabel");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.gridwidth = 2;
        gbc_label.gridx = 0;
        gbc_label.gridy = 6;
        btnAdd.addActionListener(
                e -> warehouseController.newAirplane(new Airplane(txtIdtextfield.getText(), modeltextField.getText())));

        contentPane.add(label, gbc_label);

    }

    private String getDisplayString(Airplane airplane) {
        return airplane.getId() + " - " + airplane.getModel();
    }

    @Override
    public void showError(String message, Airplane airplane) {
        label.setText(message + ": " + getDisplayString(airplane));

    }

    @Override
    public void airplaneAdded(Airplane airplane) {
        listAirplanesModel.addElement(airplane);
        resetErrorLabel();
    }

    @Override
    public void airplaneRemoved(Airplane airplane) {
        listAirplanesModel.removeElement(airplane);
        resetErrorLabel();

    }

    @Override
    public void showErrorAirplaneNotFound(String message, Airplane airplane) {
        label.setText(message + ": " + getDisplayString(airplane));
        listAirplanesModel.removeElement(airplane);

    }

    @Override
    public void showAllAirplanes(List<Airplane> airplanes) {

        airplanes.stream().forEach(listAirplanesModel::addElement);
    }

    private void resetErrorLabel() {
        label.setText(" ");
    }
}
