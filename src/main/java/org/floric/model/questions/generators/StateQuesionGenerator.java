package org.floric.model.questions.generators;

import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.StateQuestion;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/26/17.
 */
public class StateQuesionGenerator implements QuestionGenerator {
    @Override
    public Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities) {
        Set<String> states = cities.stream()
                .map(City::getStateCode)
                .collect(Collectors.toSet());
        return states.stream().map(s -> new StateQuestion(s, cities, this)).collect(Collectors.toSet());
    }
}
