package org.floric.guesser;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.util.MathUtils;
import org.floric.app.Game;
import org.floric.importer.CityImporter;
import org.floric.model.Question;
import org.floric.model.City;
import org.floric.model.QuestionGenerator;
import org.floric.model.questions.generators.*;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/13/17.
 */
public class Guesser {

    private Set<City> cities;
    private Set<City> allCities;
    private Set<String> askedQuestions;
    private Queue<Question> plannedQuestions;
    private Set<QuestionGenerator> questionGenerators;
    private Set<QuestionGenerator> maybeAnsweredGenerators;
    private int answeredQuestionsCount;
    private int askedQuestionsCount;

    private boolean needNewQuestions = true;

    public enum GuesserState {
        MULTIPLE_CITIES_LEFT,
        ONE_CITY_LEFT,
        NO_CITY_LEFT
    }

    @Data
    @AllArgsConstructor
    public class Response {
        private String text;
        private String headline;
        private boolean showCard;
        private GuesserState state;
    }

    public Guesser() {
        CityImporter importer = new CityImporter();

        try {
            // this.allCities = importer.importFromLocalFolder("/home/florian/Downloads/DE/DE.csv");
            this.allCities = importer.importFromS3("jar-artifacts", "DE.csv");
        } catch (IOException e) {
            System.err.println("File not found");
        }

        init();
    }

    public void init() {
        cities = Sets.newHashSet(allCities);
        needNewQuestions = true;
        askedQuestions = Sets.newHashSet();
        plannedQuestions = Queues.newLinkedBlockingDeque();
        askedQuestionsCount = 0;
        answeredQuestionsCount = 0;

        questionGenerators = Sets.newHashSet();
        questionGenerators.add(new DirectionsQuestionGenerator());
        questionGenerators.add(new HistoricalQuestionGenerator());
        questionGenerators.add(new NameQuestionsGenerator());
        questionGenerators.add(new PopulationQuestionGenerator());
        questionGenerators.add(new StateQuesionGenerator());

        maybeAnsweredGenerators = Sets.newHashSet();
    }

    public Response getNextQuestion() {
        if (needNewQuestions) {
            plannedQuestions.clear();
            List<Question> questions = generatePossibleQuestions();
            questions.sort(Question::compareTo);
            questions.forEach(q -> System.out.println(q.getHumanQuestion() + ": " + q.getDiscardPercentage()));
            plannedQuestions.addAll(questions);
        }

        if (cities.size() == 1) {
            String cityName = cities.stream().findFirst().get().getName();
            return new Response(
                    "Es muss " + cityName + " sein! Ich habe die Stadt in " + answeredQuestionsCount + " von " +
                            askedQuestionsCount + " Fragen gefunden. Möchtest du nochmal spielen?",
                    cityName + " erraten",
                    true,
                    GuesserState.ONE_CITY_LEFT);
        } else if (cities.isEmpty() || plannedQuestions.isEmpty()) {
            return new Response(
                    "Du hast gewonnen. Ich habe leider keine Ahnung. Möchtest du nochmal spielen?",
                    "Leider nicht erraten",
                    true,
                    GuesserState.NO_CITY_LEFT);
        }

        Question questionToUse = plannedQuestions.peek();
        askedQuestions.add(questionToUse.getHumanQuestion());
        askedQuestionsCount++;

        return new Response(
                questionToUse.getHumanQuestion(),
                "Neue Frage",
                false,
                GuesserState.MULTIPLE_CITIES_LEFT);
    }

    public void receiveResponse(Game.GameResponse response) {
        Question questionToUse = plannedQuestions.poll();

        if (response == Game.GameResponse.YES) {
            cities = questionToUse.apply();
            needNewQuestions = true;
            answeredQuestionsCount++;
        } else if (response == Game.GameResponse.NO) {
            Set<City> notMatchingCities = questionToUse.apply();
            cities = cities.stream()
                    .filter((o) -> !notMatchingCities.contains(o))
                    .collect(Collectors.toSet());
            needNewQuestions = true;
            answeredQuestionsCount++;
        } else if (response == Game.GameResponse.MAYBE){
            needNewQuestions = false;
            maybeAnsweredGenerators.add(questionToUse.getGenerator());
        }
    }

    private List<Question> generatePossibleQuestions() {
        List<Question> questions = Lists.newArrayList();

        questionGenerators.stream()
                .filter(generator -> generator.allowMoreQuestionsAfterMaybe() || !maybeAnsweredGenerators.contains(generator))
                .forEach(generator -> questions.addAll(generator.getNewQuestions(cities, allCities)));

        return questions.stream()
                .filter(q -> !askedQuestions.contains(q.getHumanQuestion()))
                .filter(q -> !MathUtils.equals(q.getDiscardPercentage(), 0) && !MathUtils.equals(q.getDiscardPercentage(), 100))
                .collect(Collectors.toList());
    }

    public static int getSummedInhabitants(Set<City> cities) {
        return (int) cities.stream().mapToDouble(City::getPopulation).sum();
    }

    public static double getRoundedDiscardPercentage(Set<City> filteredCities, Set<City> remainingCities) {
        return Math.round(Guesser.getSummedInhabitants(filteredCities) * 100.0 / Guesser.getSummedInhabitants(remainingCities));
    }
}
