package org.floric.model.questions.generators;

import com.google.common.collect.Sets;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.EasternGermanyQuestion;

import java.util.Set;

/**
 * Created by florian on 4/26/17.
 */
public class HistoricalQuestionGenerator implements QuestionGenerator {
    @Override
    public Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities) {
        return Sets.newHashSet(new EasternGermanyQuestion(cities, this));
    }
}
