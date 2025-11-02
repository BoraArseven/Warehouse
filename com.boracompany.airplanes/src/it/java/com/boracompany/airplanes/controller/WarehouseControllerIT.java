package com.boracompany.airplanes.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.boracompany.airplanes.model.Airplane;
import com.boracompany.airplanes.repository.AirplaneRepository;
import com.boracompany.airplanes.repository.mongo.AirplaneMongoRepository;
import com.boracompany.airplanes.view.AirplaneView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class WarehouseControllerIT {

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    @Mock
    private AirplaneView airplaneView;

    private AirplaneRepository airplaneRepository;

    private WarehouseController warehouseController;

    private static final String AIRPLANE_DB_NAME = "warehouse";
    private static final String AIRPLANE_COLLECTION_NAME = "airplane";

    private AutoCloseable closeable;

    private MongoClient client;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() {
        client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
        MongoDatabase database = client.getDatabase(AIRPLANE_DB_NAME);
        closeable = MockitoAnnotations.openMocks(this);
        airplaneRepository = new AirplaneMongoRepository(client, AIRPLANE_DB_NAME, AIRPLANE_COLLECTION_NAME);
        // explicit empty the database through the repository
        database.drop();
        warehouseController = new WarehouseController(airplaneView, airplaneRepository);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testAllAirplanes() {
        Airplane airplane = new Airplane("1", "test");
        airplaneRepository.save(airplane);
        warehouseController.allAirplanes();
        verify(airplaneView).showAllAirplanes(asList(airplane));
    }

    @Test
    public void testNewAirplane() {
        Airplane airplane = new Airplane("1", "test");
        warehouseController.newAirplane(airplane);
        verify(airplaneView).airplaneAdded(airplane);
    }

    @Test
    public void testDeleteAirplane() {
        Airplane airplaneToDelete = new Airplane("1", "test");
        airplaneRepository.save(airplaneToDelete);
        warehouseController.deleteAirplane(airplaneToDelete);
        verify(airplaneView).airplaneRemoved(airplaneToDelete);
    }

}
