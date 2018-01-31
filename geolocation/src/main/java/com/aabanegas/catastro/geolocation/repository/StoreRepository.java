package com.aabanegas.catastro.geolocation.repository;

import com.aabanegas.catastro.geolocation.model.Store;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface StoreRepository extends CrudRepository<Store, String> {

    static final int DESCENDING = -1;

    /**
     * Returns all {@link Store}s located withing the given {@link Polygon}.
     *
     * @param polygon must not be {@literal null}.
     * @return
     */
    List<Store> findByLocationWithin(Polygon polygon);

}