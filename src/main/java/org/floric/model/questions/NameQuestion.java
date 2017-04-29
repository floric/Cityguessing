package org.floric.model.questions;

import org.floric.guesser.Guesser;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.NameQuestionsGenerator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class NameQuestion implements Question {

    private String startChar;
    private Set<City> cities;
    private Set<City> remainingCities;
    private NameQuestionsGenerator generator;

    public NameQuestion(String startChar, Set<City> cities, NameQuestionsGenerator generator) {
        this.startChar = startChar;
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> c.getName().startsWith(startChar))
                .collect(Collectors.toSet());
        this.generator = generator;
    }

    @Override
    public String getHumanQuestion() {
        return "Beginnt der Name deiner Stadt mit \"" + startChar + "\"?";
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
