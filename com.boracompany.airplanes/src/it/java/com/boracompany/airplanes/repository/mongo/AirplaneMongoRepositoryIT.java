package com.boracompany.airplanes.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.boracompany.airplanes.model.Airplane;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AirplaneMongoRepositoryIT {

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    private MongoClient client;

    private AirplaneMongoRepository airplaneRepository;
    private MongoCollection<Document> airplaneCollection;
    private static final String AIRPLANE_DB_NAME = "warehouse";
    private static final String AIRPLANE_COLLECTION_NAME = "airplane";

    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
        airplaneRepository = new AirplaneMongoRepository(client, AIRPLANE_DB_NAME, AIRPLANE_COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(AIRPLANE_DB_NAME);
        // make sure we always start with a clean database
        database.drop();
        airplaneCollection = database.getCollection(AIRPLANE_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAll() {
        addTestAirplaneToDatabase("1", "test1");
        addTestAirplaneToDatabase("2", "test2");
        assertThat(airplaneRepository.findAll()).containsExactly(new Airplane("1", "test1"),
                new Airplane("2", "test2"));
    }

    @Test
    public void testFindAllReturnsMappedAirplanes() {
        // Arrange: insert known Airplane documents into airplaneCollection
        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        addTestAirplaneToDatabase(airplane1.getId(), airplane1.getModel());
        addTestAirplaneToDatabase(airplane2.getId(), airplane2.getModel());

        // Act
        List<Airplane> result = airplaneRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("test1", result.get(0).getModel());
        assertEquals("2", result.get(1).getId());
        assertEquals("test2", result.get(1).getModel());
    }

    @Test
    public void testSave() {
        Airplane airplane = new Airplane("1", "added airplane");
        airplaneRepository.save(airplane);
        assertThat(readAllAirplanesFromDatabase()).containsExactly(airplane);
    }

    @Test
    public void testDelete() {
        addTestAirplaneToDatabase("1", "test1");
        airplaneRepository.delete("1");
        assertThat(readAllAirplanesFromDatabase()).isEmpty();
    }

    private void addTestAirplaneToDatabase(String id, String model) {
        airplaneCollection.insertOne(new Document().append("id", id).append("model", model));
    }

    private List<Airplane> readAllAirplanesFromDatabase() {
        return StreamSupport.stream(airplaneCollection.find().spliterator(), false)
                .map(d -> new Airplane("" + d.get("id"), "" + d.get("model"))).toList();
    }

}
