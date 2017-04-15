package org.floric.guesser;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Pair;
import org.floric.app.Game;
import org.floric.importer.CityImporter;
import org.floric.model.Askable;
import org.floric.model.City;
import org.floric.model.questions.*;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/13/17.
 */
public class Guesser {

    private List<City> cities;
    private List<City> allCities;
    private Set<String> askedQuestions = Sets.newHashSet();
    private Queue<Askable> plannedQuestions = Queues.newLinkedBlockingDeque();

    private boolean needNewQuestions = true;
    private boolean finishedGuessing = false;

    public Guesser() {
        CityImporter importer = new CityImporter();

        this.allCities = importer.importCsvFile("DE.csv");

        restart();
    }

    public boolean isGuessingFinished() {
        return finishedGuessing;
    }

    public void restart() {
        this.cities = Lists.newArrayList(allCities);
        this.needNewQuestions = true;
        this.askedQuestions = Sets.newHashSet();
        this.plannedQuestions = Queues.newLinkedBlockingDeque();
        this.finishedGuessing = false;
    }

    public String getNextQuestion() {
        if (needNewQuestions) {
            plannedQuestions.clear();
            List<Askable> questions = generatePossibleQuestions();
            questions.sort(Askable::compareTo);
            plannedQuestions.addAll(questions);
        }

        if (cities.size() == 1) {
            finishedGuessing = true;
            return ("Es muss " + cities.get(0).getName() + " sein! Willst du nochmal spielen?");
        } else if (cities.isEmpty() || plannedQuestions.isEmpty()) {
            finishedGuessing = true;
            return ("Du hast gewonnen. Ich habe leider keine Ahnung. Willst du nochmal spielen?");
        }

        Askable questionToUse = plannedQuestions.peek();
        askedQuestions.add(questionToUse.getHumanQuestion());

        return questionToUse.getHumanQuestion();
    }

    public void receiveResponse(Game.GameResponse response) {
        Askable questionToUse = plannedQuestions.poll();

        if (response == Game.GameResponse.YES) {
            cities = questionToUse.apply();
            needNewQuestions = true;
        } else if (response == Game.GameResponse.NO) {
            List<City> notMatchingCities = questionToUse.apply();
            cities = cities.stream()
                    .filter((o) -> !notMatchingCities.contains(o))
                    .collect(Collectors.toList());
            needNewQuestions = true;
        } else if (response == Game.GameResponse.MAYBE){
            needNewQuestions = false;
        }
    }

    private List<Askable> generatePossibleQuestions() {
        List<Askable> questions = Lists.newArrayList();

        Pair<Vector2D, Vector2D> area = getPossibleArea();

        questions.add(new PopulationQuestion(cities));

        Set<String> states = cities.stream()
                .map(City::getStateCode)
                .collect(Collectors.toSet());
        states.forEach(s -> questions.add(new StateQuestion(s, cities)));

        Set<String> letters = cities.stream()
                .map(c -> c.getName().substring(0, 1))
                .collect(Collectors.toSet());
        letters.forEach(l -> questions.add(new NameQuestion(l, cities)));

        // take ten biggest cities in search area as a reference
        List<City> orientationalCities = allCities.stream()
                .filter(c -> c.getCoordinate().getX() < area.getKey().getX() &&
                        c.getCoordinate().getX() > area.getValue().getX() &&
                        c.getCoordinate().getY() > area.getKey().getY() &&
                        c.getCoordinate().getY() < area.getValue().getY()
                )
                .sorted((a, b) -> -Double.compare(a.getPopulation(), b.getPopulation()))
                .limit(10)
                .collect(Collectors.toList());
        orientationalCities.forEach(c -> questions.add(new DirectionsQuestion(c, cities)));

        questions.add(new EasternGermanyQuestion(cities));

        return questions.stream()
                .filter(q -> !askedQuestions.contains(q.getHumanQuestion()))
                .filter(q -> !MathUtils.equals(q.getDiscardPercentage(), 0) && !MathUtils.equals(q.getDiscardPercentage(), 100))
                .collect(Collectors.toList());
    }

    private Pair<Vector2D, Vector2D> getPossibleArea() {
        double northBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getX()).max().orElse(0);
        double southBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getX()).min().orElse(0);
        double westBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getY()).min().orElse(0);
        double eastBorder = cities.stream().mapToDouble(c -> c.getCoordinate().getY()).max().orElse(0);
        return Pair.create(new Vector2D(northBorder, westBorder), new Vector2D(southBorder, eastBorder));
    }

    public static int getSummedInhabitants(List<City> cities) {
        return (int) cities.stream().mapToDouble(City::getPopulation).sum();
    }

    public static double getDiscardPercentage(List<City> filteredCities, List<City> remainingCities) {
        return Guesser.getSummedInhabitants(filteredCities) * 100.0 / Guesser.getSummedInhabitants(remainingCities);
    }
}
