package org.floric.model.questions.generators;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Pair;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.DirectionsQuestion;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/26/17.
 */
public class DirectionsQuestionGenerator implements QuestionGenerator {
    @Override
    public Set<Question> getNewQuestions(Set<City> cities, Set<City> allCities) {
        Pair<Vector2D, Vector2D> area = getPossibleArea(cities);
        Vector2D center = getCenter(area.getFirst(), area.getSecond());

        // take ten biggest cities in search area as a reference
        List<City> orientationalCities = allCities.stream()
                .filter(c -> c.getCoordinate().getX() < area.getKey().getX() &&
                        c.getCoordinate().getX() > area.getValue().getX() &&
                        c.getCoordinate().getY() > area.getKey().getY() &&
                        c.getCoordinate().getY() < area.getValue().getY()
                )
                .sorted((a, b) -> -Double.compare(a.getPopulation(), b.getPopulation()))
                .filter(c -> c.getPopulation() > 50000)
                .limit(20) // Take biggest cities
                .collect(Collectors.toList());

        return orientationalCities.stream()
                .map(c -> new DirectionsQuestion(c, cities, this))
                .collect(Collectors.toSet());
    }

    private Pair<Vector2D, Vector2D> getPossibleArea(Set<City> cities) {
        double northBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getX()).max().orElse(0);
        double southBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getX()).min().orElse(0);
        double westBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getY()).min().orElse(0);
        double eastBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getY()).max().orElse(0);
        return Pair.create(new Vector2D(northBorder, westBorder), new Vector2D(southBorder, eastBorder));
    }

    private Vector2D getCenter(Vector2D northWestCorner, Vector2D southEastCorner) {
        return new Vector2D((northWestCorner.getX() - southEastCorner.getX()) / 2 + southEastCorner.getX(),
                (northWestCorner.getY() - southEastCorner.getY()) / 2 + southEastCorner.getY());
    }
}
