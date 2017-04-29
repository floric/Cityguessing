package org.floric.model.questions;

import com.google.common.collect.Sets;
import org.floric.guesser.Guesser;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.HistoricalQuestionGenerator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class EasternGermanyQuestion implements Question {

    private Set<City> cities;
    private Set<City> remainingCities;
    private static final Set<String> EASTERN_STATES = Sets.newHashSet(
            "Thüringen", "Sachsen", "Sachsen-Anhalt", "Berlin", "Brandenburg", "Mecklemburg-Vorpommern");
    private HistoricalQuestionGenerator generator;

    public EasternGermanyQuestion(Set<City> cities, HistoricalQuestionGenerator generator) {
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> EASTERN_STATES.contains(c.getStateCode()))
                .collect(Collectors.toSet());
        this.generator = generator;
    }

    @Override
    public String getHumanQuestion() {
        return "Ist die Stadt Teil der ehemaligen DDR?";
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
