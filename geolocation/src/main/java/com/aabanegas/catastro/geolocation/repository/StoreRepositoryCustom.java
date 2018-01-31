package com.aabanegas.catastro.geolocation.repository;

import com.aabanegas.catastro.geolocation.model.Store;
import java.util.List;
import org.bson.Document;
import org.springframework.data.geo.Polygon;
import org.springframework.data.repository.CrudRepository;

interface StoreRepositoryCustom {

    static final int DESCENDING = -1;

    Document encode(final Store store);

    Store decode(final Document document) throws Exception;

}