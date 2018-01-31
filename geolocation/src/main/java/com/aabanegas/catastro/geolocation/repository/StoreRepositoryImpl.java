package com.aabanegas.catastro.geolocation.repository;

import com.aabanegas.catastro.geolocation.exception.ParseLocationException;
import com.aabanegas.catastro.geolocation.model.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.repository.CrudRepository;

public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private static final String COLLECTION_NAME = "starbucks";
    private final MongoCollection<Document> collection;

    @Autowired
    public StoreRepositoryImpl(final MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    @Override
    public Document encode(final Store store) {
        final Document document = new Document()
                .append("id", store.getId())
                .append("name", store.getName())
                .append("street", store.getStreet())
                .append("location", writeLocation(store.getLocation()));
        return document;
    }

    @Override
    public Store decode(final Document document) throws Exception{
        Optional<GeoJsonPoint> point = readLocation(document.getString("location"));

        GeoJsonPoint geoJsonPoint = point
                .orElseThrow(() -> new ParseLocationException(String.format("Invalid values %s",
                        document.getString("location"))));

        return new Store(document.getString("id"),
                document.getString("name"),
                document.getString("street"),
                document.getString("city"),
                geoJsonPoint);
    }

    private Optional<GeoJsonPoint> readLocation(String location) {
        GeoJsonPoint point = null;
        try {
            point = new ObjectMapper().readValue(location, GeoJsonPoint.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(point);
    }

    private Optional<String> writeLocation(GeoJsonPoint location) {
        String value = null;
        try {
            value = new ObjectMapper().writeValueAsString(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(value);
    }

}