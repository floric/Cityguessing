package org.floric.model;

import java.util.Set;

/**
 * Created by florian on 4/26/17.
 */
public interface QuestionGenerator {

    Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities);
    default boolean allowMoreQuestionsAfterMaybe() {
        return true;
    }
}
