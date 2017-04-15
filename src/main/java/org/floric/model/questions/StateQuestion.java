package org.floric.model.questions;

import org.floric.guesser.Guesser;
import org.floric.model.Askable;
import org.floric.model.City;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class StateQuestion implements Askable {

    private String stateName;
    private List<City> cities;
    private List<City> remainingCities;

    public StateQuestion(String stateName, List<City> cities) {
        this.cities = cities;
        this.stateName = stateName;
        this.remainingCities = cities.stream()
                .filter(c -> c.getStateCode().equals(stateName))
                .collect(Collectors.toList());
    }

    @Override
    public String getHumanQuestion() {
        return "Is your city part of " + stateName + "?";
    }

    @Override
    public List<City> apply() {
        return remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getDiscardPercentage(remainingCities, cities);
    }
}
