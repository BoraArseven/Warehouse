package com.boracompany.airplanes.repository.mongo;

import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.boracompany.airplanes.guice.MongoCollectionName;
import com.boracompany.airplanes.guice.MongoDbName;
import com.boracompany.airplanes.model.Airplane;
import com.boracompany.airplanes.repository.AirplaneRepository;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class AirplaneMongoRepository implements AirplaneRepository {

    private MongoCollection<Document> airplaneCollection;

    @Inject
    public AirplaneMongoRepository(MongoClient client, @MongoDbName String airplaneDbName,
            @MongoCollectionName String airplaneCollectionName) {
        airplaneCollection = client.getDatabase(airplaneDbName).getCollection(airplaneCollectionName);
    }

    @Override
    public List<Airplane> findAll() {

        return StreamSupport.stream(airplaneCollection.find().spliterator(), false).map(this::fromDocumentToAirplane)
                .toList();

    }

    private Airplane fromDocumentToAirplane(Document d) {
        return new Airplane("" + d.get("id"), "" + d.get("model"));
    }

    @Override
    public Airplane findById(String id) {
        Document d = airplaneCollection.find(Filters.eq("id", id)).first();
        if (d != null)
            return fromDocumentToAirplane(d);
        return null;
    }

    @Override
    public void save(Airplane airplane) {
        airplaneCollection
                .insertOne(new Document().append("id", airplane.getId()).append("model", airplane.getModel()));
    }

    @Override
    public void delete(String id) {
        airplaneCollection.deleteOne(Filters.eq("id", id));

    }

}
