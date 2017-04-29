package org.floric.model.questions;

import org.floric.guesser.FilterFinder;
import org.floric.guesser.Guesser;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.PopulationQuestionGenerator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class PopulationQuestion implements Question {

    private int populationThreshold = 0;
    private Set<City> cities;
    private Set<City> remainingCities;
    private PopulationQuestionGenerator generator;

    public PopulationQuestion(Set<City> cities, PopulationQuestionGenerator generator) {
        FilterFinder filterFinder = new FilterFinder();

        populationThreshold = getRoundedPopulation(filterFinder.getFilterValue(cities, c -> (double) c.getPopulation()));
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> c.getPopulation() > populationThreshold)
                .collect(Collectors.toSet());
        this.generator = generator;
    }

    @Override
    public String getHumanQuestion() {
        return "Hat deine Stadt mehr als " + populationThreshold + " Einwohner?";
    }

    @Override
    public Set<City> apply() {
        return remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getRoundedDiscardPercentage(remainingCities, cities);
    }

    @Override
    public QuestionGenerator getGenerator() {
        return generator;
    }

    private int getRoundedPopulation(double populationThreshold) {
        return ((int) Math.ceil(populationThreshold / 100)) * 100;
    }
}
