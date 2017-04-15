package org.floric.model.questions;

import com.google.common.collect.Maps;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Pair;
import org.floric.guesser.Guesser;
import org.floric.model.Askable;
import org.floric.model.City;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class DirectionsQuestion implements Askable {

    private static final String NORTH = "north";
    private static final String EAST = "east";

    private List<City> cities;
    private List<City> remainingCities;
    private City referenceCity;
    private Map<String, Function<Vector2D, Double>> directions = Maps.newHashMap();
    private String direction = "";

    public DirectionsQuestion(City referenceCity, List<City> cities) {
        directions.put(NORTH, Vector2D::getX);
        directions.put(EAST, Vector2D::getY);

        this.cities = cities;
        this.referenceCity = referenceCity;

        Map<String, List<City>> mappedDirections = directions.entrySet().stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        cities.stream()
                                .filter(c -> entry.getValue().apply(c.getCoordinate()) > entry.getValue().apply(referenceCity.getCoordinate()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        double northDiscardValue = Guesser.getDiscardPercentage(mappedDirections.get(NORTH), cities);
        double eastDiscardValue = Guesser.getDiscardPercentage(mappedDirections.get(EAST), cities);

        boolean useNorth = Math.abs(50 - northDiscardValue) < Math.abs(50 - eastDiscardValue);

        this.remainingCities = useNorth ? mappedDirections.get(NORTH) : mappedDirections.get(EAST);
        this.direction = useNorth ? NORTH : EAST;
    }

    @Override
    public String getHumanQuestion() {
        return "Is the city more " + direction + " then " + referenceCity.getName() + "?";
    }

    @Override
    public List<City> apply() {
        return this.remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getDiscardPercentage(remainingCities, cities);
    }
}
