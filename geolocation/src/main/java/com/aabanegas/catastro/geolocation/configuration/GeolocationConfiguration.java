package com.aabanegas.catastro.geolocation.configuration;

import com.aabanegas.catastro.geolocation.model.Store;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import java.io.IOException;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.init.AbstractRepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@EnableMongoRepositories(basePackages = "com.aabanegas.catastro.geolocation.repository")
@Configuration
public class GeolocationConfiguration {

    private static final String MONGO_DB_URL = "localhost";
    private static final String MONGO_DB_NAME = "embeded_db";

    MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp(MONGO_DB_URL);
        mongoClient = mongo.getObject();
        return mongoClient;
    }

    @Bean
    public MongoDatabase database() throws IOException {
        MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = database.getCollection("stores");
        collection.createIndex(Indexes.geo2dsphere("store.location"));
        return database;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGO_DB_NAME);
        return mongoTemplate;
    }

    /**
     * Read JSON data from disk and insert those stores.
     *
     * @return
     */
    public @Bean AbstractRepositoryPopulatorFactoryBean repositoryPopulator() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(GeoJsonPoint.class, GeoJsonPointMixin.class);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
        factoryBean.setResources(new Resource[] { new ClassPathResource("starbucks-in-nyc.json") });
        factoryBean.setMapper(mapper);

        return factoryBean;
    }

    static abstract class GeoJsonPointMixin {
        GeoJsonPointMixin(@JsonProperty("longitude") double x, @JsonProperty("latitude") double y) {}
    }
}
