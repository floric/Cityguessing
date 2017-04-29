package org.floric.model;

import java.util.Set;

/**
 * Created by florian on 4/14/17.
 */
public interface Question extends Comparable<Question> {

    String getHumanQuestion();
    Set<City>
    apply();
    double getDiscardPercentage();
    QuestionGenerator getGenerator();

    default int compareTo(Question o) {
        return Double.compare(Math.abs(50.0 - getDiscardPercentage()), Math.abs(50.0 - o.getDiscardPercentage()));
    }
}
