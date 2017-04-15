package org.floric.model.questions;

import org.floric.guesser.FilterFinder;
import org.floric.guesser.Guesser;
import org.floric.model.Askable;
import org.floric.model.City;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class PopulationQuestion implements Askable {

    private int populationThreshold = 0;
    private List<City> cities;
    private List<City> remainingCities;

    public PopulationQuestion(List<City> cities) {
        FilterFinder filterFinder = new FilterFinder();

        populationThreshold = getRoundedPopulation(filterFinder.getFilterValue(cities, c -> (double) c.getPopulation()));
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> c.getPopulation() > populationThreshold)
                .collect(Collectors.toList());
    }

    @Override
    public String getHumanQuestion() {
        return "Does your city have more then " + populationThreshold + " inhabitants?";
    }

    @Override
    public List<City> apply() {
        return remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getDiscardPercentage(remainingCities, cities);
    }

    private int getRoundedPopulation(double populationThreshold) {
        return ((int) Math.ceil(populationThreshold / 100)) * 100;
    }
}
