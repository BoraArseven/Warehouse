package com.boracompany.airplanes.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.boracompany.airplanes.model.Airplane;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

class AirplaneMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient client;
    private AirplaneMongoRepository airplaneRepository;
    private MongoCollection<Document> airplaneCollection;

    private static final String AIRPLANE_DB_NAME = "warehouse";
    private static final String AIRPLANE_COLLECTION_NAME = "airplane";

    @BeforeAll
    static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterAll
    static void shutdownServer() {
        server.shutdown();
    }

    @BeforeEach
    void setUp() {
        client = new MongoClient(new ServerAddress(serverAddress));
        airplaneRepository = new AirplaneMongoRepository(client, AIRPLANE_DB_NAME, AIRPLANE_COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(AIRPLANE_DB_NAME);
        database.drop();
        airplaneCollection = database.getCollection(AIRPLANE_COLLECTION_NAME);
    }

    @AfterEach
    void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        assertThat(airplaneRepository.findAll()).isEmpty();
    }

    @Test
    public void testFindALlWhenDatabaseIsNotEmpty() {

        addTestAirplaneToDatabase("1", "test1");
        addTestAirplaneToDatabase("2", "test2");
        assertThat(airplaneRepository.findAll()).containsExactly(new Airplane("1", "test1"),
                new Airplane("2", "test2"));

    }

    private void addTestAirplaneToDatabase(String id, String model) {
        airplaneCollection.insertOne(new Document().append("id", id).append("model", model));
    }

    @Test
    public void testFindByIdNotFound() {
        assertThat(airplaneRepository.findById("1")).isNull();
    }

    @Test
    public void testFindByIdFound() {
        addTestAirplaneToDatabase("1", "test1");
        addTestAirplaneToDatabase("2", "test2");
        assertThat(airplaneRepository.findById("2")).isEqualTo(new Airplane("2", "test2"));
    }

    @Test
    public void testSave() {
        Airplane airplane = new Airplane("1", "added airplane");
        airplaneRepository.save(airplane);
        assertThat(readAllAirplanesFromDatabase()).containsExactly(airplane);
    }

    private List<Airplane> readAllAirplanesFromDatabase() {
        return StreamSupport.stream(airplaneCollection.find().spliterator(), false)
                .map(d -> new Airplane("" + d.get("id"), "" + d.get("model"))).collect(Collectors.toList());
    }

    @Test
    public void testDelete() {
        addTestAirplaneToDatabase("1", "test1");
        airplaneRepository.delete("1");
        assertThat(readAllAirplanesFromDatabase()).isEmpty();
    }
}
