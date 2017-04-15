package org.floric.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * Created by florian on 4/13/17.
 */
@Data
@AllArgsConstructor
public class City {
    private String name;
    private Vector2D coordinate;
    private double altitude;
    private int population;
    private List<String> alternateNames;
    private String countryCode;
    private String stateCode;
}
