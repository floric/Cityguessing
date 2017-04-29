package org.floric.model.questions.generators;

import com.google.common.collect.Sets;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.PopulationQuestion;

import java.util.Set;

/**
 * Created by florian on 4/26/17.
 */
public class PopulationQuestionGenerator implements QuestionGenerator {

    @Override
    public Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities) {
        return Sets.newHashSet(new PopulationQuestion(cities, this));
    }

    @Override
    public boolean allowMoreQuestionsAfterMaybe() {
        return false;
    }
}
