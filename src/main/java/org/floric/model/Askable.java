package org.floric.model;

import java.util.List;

/**
 * Created by florian on 4/14/17.
 */
public interface Askable extends Comparable<Askable> {

    String getHumanQuestion();
    List<City> apply();
    double getDiscardPercentage();

    default int compareTo(Askable o) {
        return Double.compare(Math.abs(50.0 - getDiscardPercentage()), Math.abs(50.0 - o.getDiscardPercentage()));
    }
}
