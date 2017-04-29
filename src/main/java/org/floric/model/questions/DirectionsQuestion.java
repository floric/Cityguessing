package org.floric.model.questions;

import com.google.common.collect.Maps;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Pair;
import org.floric.guesser.Guesser;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.DirectionsQuestionGenerator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class DirectionsQuestion implements Question {

    private static final String NORTH = "nördlich";
    private static final String EAST = "östlich";

    private Set<City> cities;
    private Set<City> remainingCities;
    private City referenceCity;
    private Map<String, Function<Vector2D, Double>> directions = Maps.newHashMap();
    private String direction = "";
    private DirectionsQuestionGenerator generator;

    public DirectionsQuestion(City referenceCity, Set<City> cities, DirectionsQuestionGenerator generator) {
        directions.put(NORTH, Vector2D::getX);
        directions.put(EAST, Vector2D::getY);

        this.cities = cities;
        this.referenceCity = referenceCity;

        Map<String, Set<City>> mappedDirections = directions.entrySet().stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        cities.stream()
                                .filter(c -> entry.getValue().apply(c.getCoordinate()) > entry.getValue().apply(referenceCity.getCoordinate()))
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        double northDiscardValue = Guesser.getRoundedDiscardPercentage(mappedDirections.get(NORTH), cities);
        double eastDiscardValue = Guesser.getRoundedDiscardPercentage(mappedDirections.get(EAST), cities);

        boolean useNorth = Math.abs(50 - northDiscardValue) < Math.abs(50 - eastDiscardValue);

        this.remainingCities = useNorth ? mappedDirections.get(NORTH) : mappedDirections.get(EAST);
        this.direction = useNorth ? NORTH : EAST;
        this.generator = generator;
    }

    @Override
    public String getHumanQuestion() {
        return "Ist die Stadt " + direction + " von " + referenceCity.getName() + "?";
    }

    @Override
    public Set<City> apply() {
        return this.remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getRoundedDiscardPercentage(remainingCities, cities);
    }

    @Override
    public QuestionGenerator getGenerator() {
        return generator;
    }
}
