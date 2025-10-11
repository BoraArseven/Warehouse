package com.boracompany.airplanes.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.boracompany.airplanes.guice.AirplaneSwingMongoDefaultModule;
import com.boracompany.airplanes.view.swing.AirplaneSwingView;
import com.google.inject.Guice;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class WarehouseSwingApp implements Callable<Void> {

    @Option(names = { "--mongo-host" }, description = "MongoDB host address")
    private String mongoHost = "localhost";

    @Option(names = { "--mongo-port" }, description = "MongoDB host port")
    private int mongoPort = 27017;

    @Option(names = { "--db-name" }, description = "Database name")
    private String databaseName = "warehouse";

    @Option(names = { "--db-collection" }, description = "Collection name")
    private String collectionName = "airplane";

    public static void main(String[] args) {
        new CommandLine(new WarehouseSwingApp()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                Guice.createInjector(new AirplaneSwingMongoDefaultModule().mongoHost(mongoHost).mongoPort(mongoPort)
                        .databaseName(databaseName).collectionName(collectionName)).getInstance(AirplaneSwingView.class)
                        .start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

}