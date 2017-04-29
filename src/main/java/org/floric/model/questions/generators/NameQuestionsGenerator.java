package org.floric.model.questions.generators;

import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.NameQuestion;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/26/17.
 */
public class NameQuestionsGenerator implements QuestionGenerator {
    @Override
    public Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities) {
        Set<String> letters = cities.stream()
                .map(c -> c.getName().substring(0, 1))
                .collect(Collectors.toSet());

        return letters.stream().map(l -> new NameQuestion(l, cities, this)).collect(Collectors.toSet());
    }
}
