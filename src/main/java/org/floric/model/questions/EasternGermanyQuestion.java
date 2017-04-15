package org.floric.model.questions;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.floric.guesser.Guesser;
import org.floric.model.Askable;
import org.floric.model.City;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/14/17.
 */
public class EasternGermanyQuestion implements Askable {

    private List<City> cities;
    private List<City> remainingCities;
    private static final Set<String> EASTERN_STATES = Sets.newHashSet(
            "Thüringen", "Sachsen", "Sachsen-Anhalt", "Berlin", "Brandenburg", "Mecklemburg-Vorpommern");

    public EasternGermanyQuestion(List<City> cities) {
        this.cities = cities;
        this.remainingCities = cities.stream()
                .filter(c -> EASTERN_STATES.contains(c.getStateCode()))
                .collect(Collectors.toList());
    }

    @Override
    public String getHumanQuestion() {
        return "Ist die Stadt Teil der ehemaligen DDR?";
    }

    @Override
    public List<City> apply() {
        return remainingCities;
    }

    @Override
    public double getDiscardPercentage() {
        return Guesser.getDiscardPercentage(remainingCities, cities);
    }
}
