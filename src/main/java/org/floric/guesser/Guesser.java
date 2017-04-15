package org.floric.guesser;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Pair;
import org.floric.importer.CityImporter;
import org.floric.model.Askable;
import org.floric.model.City;
import org.floric.model.questions.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
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

    boolean needNewQuestions = true;

    public enum GuessResponse {
        YES,
        NO,
        MAYBE
    }

    public Guesser() throws FileNotFoundException {
        CityImporter importer = new CityImporter();
        this.allCities = importer.importCsvFile("/home/florian/Downloads/DE/DE.csv");
        this.cities = Lists.newArrayList(allCities);
    }

    public void restart() {
        this.cities = Lists.newArrayList(allCities);
    }

    public String getNextQuestion() {

    }

    public void receiveResponse(GuessResponse response) {

    }

    public void start() {
        int iterations = 1;

        while(true) {
            if (needNewQuestions) {
                plannedQuestions.clear();
                List<Askable> questions = generatePossibleQuestions();
                questions.sort(Askable::compareTo);
                plannedQuestions.addAll(questions);
            }

            if (cities.size() == 1) {
                System.out.println("It needs to be " + cities.get(0).getName() + "!");
                break;
            } else if (cities.isEmpty()) {
                System.out.println("I don't know what you have had in mind!");
                break;
            } else if (plannedQuestions.isEmpty()) {
                System.out.println("I have no idea which city you had picked...");
                break;
            }

            // Next planned questions:
            // questions.forEach(q -> System.out.println(q.getHumanQuestion() + " -> " + q.getDiscardPercentage()));

            Askable questionToUse = plannedQuestions.poll();
            System.out.println(iterations + ") Alexa asks: " + questionToUse.getHumanQuestion());
            if (cities.size() < 10) {
                System.out.println("Left: " + cities.stream().map(City::getName).reduce((a, b) -> a + ", " + b));
            }

            askedQuestions.add(questionToUse.getHumanQuestion());

            Scanner keyboard = new Scanner(System.in);
            System.out.println("Please answer with: y (yes) | n (no) | m (maybe, not sure)");
            String input = keyboard.next();

            if (input.equals("y")) {
                cities = questionToUse.apply();
                needNewQuestions = true;
            } else if (input.equals("n")) {
                List<City> notMatchingCities = questionToUse.apply();
                cities = cities.stream().filter((o) -> !notMatchingCities.contains(o)).collect(Collectors.toList());
                needNewQuestions = true;
            } else {
                needNewQuestions = false;
            }

            iterations++;
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
