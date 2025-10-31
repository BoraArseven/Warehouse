package com.boracompany.airplanes.guice;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.repository.AirplaneRepository;
import com.boracompany.airplanes.repository.mongo.AirplaneMongoRepository;
import com.boracompany.airplanes.view.swing.AirplaneSwingView;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongodb.MongoClient;

public class AirplaneSwingMongoDefaultModule extends AbstractModule {

    private String mongoHost = "localhost";

    private int mongoPort = 27017;

    private String databaseName = "Warehouse";

    private String collectionName = "Airplane";

    public AirplaneSwingMongoDefaultModule mongoHost(String mongoHost) {
        this.mongoHost = mongoHost;
        return this;
    }

    public AirplaneSwingMongoDefaultModule mongoPort(int mongoPort) {
        this.mongoPort = mongoPort;
        return this;
    }

    public AirplaneSwingMongoDefaultModule databaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public AirplaneSwingMongoDefaultModule collectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
        bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
        bind(String.class).annotatedWith(MongoDbName.class).toInstance(databaseName);
        bind(String.class).annotatedWith(MongoCollectionName.class).toInstance(collectionName);

        bind(AirplaneRepository.class).to(AirplaneMongoRepository.class);

        install(new FactoryModuleBuilder().implement(WarehouseController.class, WarehouseController.class)
                .build(WarehouseControllerFactory.class));
    }

    @Provides
    MongoClient mongoClient(@MongoHost String host, @MongoPort int port) {
        return new MongoClient(host, port);
    }

    @Provides
    AirplaneSwingView airplaneView(WarehouseControllerFactory warehouseControllerFactory) {
        AirplaneSwingView airplaneSwingView = new AirplaneSwingView();
        airplaneSwingView.setWarehouseController(warehouseControllerFactory.create(airplaneSwingView));
        return airplaneSwingView;
    }
}
