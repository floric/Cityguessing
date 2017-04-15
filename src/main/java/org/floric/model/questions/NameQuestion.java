package org.floric.model.questions;

import org.floric.guesser.Guesser;
import org.floric.model.Askable;
import org.floric.model.City;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class NameQuestion implements Askable {

    private String startChar;
    private List<City> cities;
    private List<City> remainingCities;

    public NameQuestion(String startChar, List<City> cities) {
        this.startChar = startChar;
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> c.getName().startsWith(startChar))
                .collect(Collectors.toList());
    }

    @Override
    public String getHumanQuestion() {
        return "Beginnt der Name deiner Stadt mit \"" + startChar + "\"?";
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
