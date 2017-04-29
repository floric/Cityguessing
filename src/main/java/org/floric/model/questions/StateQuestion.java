package org.floric.model.questions;

import org.floric.guesser.Guesser;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.StateQuesionGenerator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class StateQuestion implements Question {

    private String stateName;
    private Set<City> cities;
    private Set<City> remainingCities;
    private StateQuesionGenerator generator;

    public StateQuestion(String stateName, Set<City> cities, StateQuesionGenerator generator) {
        this.cities = cities;
        this.stateName = stateName;
        this.remainingCities = cities.stream()
                .filter(c -> c.getStateCode().equals(stateName))
                .collect(Collectors.toSet());
        this.generator = generator;
    }

    @Override
    public String getHumanQuestion() {
        return "Ist deine Stadt Teil von " + stateName + "?";
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
}
