package com.aabanegas.catastro.geolocation.repository;

import com.aabanegas.catastro.geolocation.model.Store;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for {@link StoreRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryTests {

    private static final GeoJsonPolygon GEO_JSON_POLYGON = new GeoJsonPolygon(new Point(-73.992514, 40.758934),
            new Point(-73.961138, 40.760348), new Point(-73.991658, 40.730006), new Point(-73.992514, 40.758934));

    private static final com.mongodb.client.model.geojson.Point GEO_JSON_POINT = new com.mongodb.client.model.geojson.Point(new com.mongodb.client.model.geojson.Position(-73.992514,
            40.758934));

    @Autowired
    StoreRepository repository;
    @Autowired
    MongoOperations operations;
    @Autowired
    MongoDatabase database;

    @Test
    public void findWithinGeoJsonPoint() {
        Point location = new Point(-73.99171, 40.738868);
        NearQuery near = NearQuery.near(location).maxDistance(new Distance(10, Metrics.MILES));
        GeoResults<Store> stores = operations.geoNear(near, Store.class);
        stores.forEach(System.out::println);
    }

    @Test
    public void findWithinGeoJsonPolygon() {
        repository.findByLocationWithin(GEO_JSON_POLYGON).forEach(System.out::println);
    }

    @Test
    public void findWithinLegacyPolygon() {
        repository.findByLocationWithin(new Polygon(new Point(-73.992514, 40.758934), new Point(-73.961138, 40.760348),
                new Point(-73.991658, 40.730006))).forEach(System.out::println);
    }

    @Test
    public void findRestaurantsNearAGivenPoint() {
        /** @see https://docs.spring.io/spring-data/data-mongodb/docs/current/reference/pdf/spring-data-mongodb-reference.pdf
         * @see http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/geospatial-search/
         * https://www.programcreek.com/java-api-examples/index
         * .php?source_dir=gennai-master/gungnir/core/src/main/java/org/gennai/gungnir/topology/processor/MongoFetchProcessor.java
         * */
        // import com.mongodb.client.model.geojson.Point;
        //Point refPoint = new Point(new Position(-73.9667, 40.78));
        MongoCollection<Document> collection = database.getCollection("restaurants");

        FindIterable<Document> search = collection.find(Filters.near("contact.location", GEO_JSON_POINT,
                new Distance(10, Metrics.KILOMETERS).getNormalizedValue(),
                new Distance(10, Metrics.KILOMETERS).getValue()));

        for (Document current : search) {
            System.out.println(current.toJson());
//            Store store = new Store();
//            System.out.println(current.getString("id");
//            System.out.println(current.getString("name");
//            System.out.println(current.getString("city");
        }
    }
}