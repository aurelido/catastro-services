package com.aabanegas.catastro.geolocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christoph Strobl
 */
@Data
@AllArgsConstructor
@Document(collection = "starbucks")
public class Store {

    String id;
    String name;
    String street;
    String city;

    /**
     * {@code location} is stored in GeoJSON format.
     *
     * <pre>
     * <code>
     * {
     *   "type" : "Point",
     *   "coordinates" : [ x, y ]
     * }
     * </code>
     * </pre>
     */
    GeoJsonPoint location;
}
