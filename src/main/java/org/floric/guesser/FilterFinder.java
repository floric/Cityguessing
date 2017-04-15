package org.floric.guesser;

import org.apache.commons.math3.stat.StatUtils;
import org.floric.model.City;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by florian on 4/14/17.
 */
public class FilterFinder {

    public FilterFinder() {

    }

    public double getFilterValue(List<City> cities, Function<City, Double> mapCityToScalarValue) {
        double[] mappedCities = cities.stream().mapToDouble(mapCityToScalarValue::apply).toArray();

        double middleVal = StatUtils.mean(mappedCities);
        double prevMiddleVal = 0;
        double upFactor = 1;
        boolean wasToLow = false;

        while (Math.abs(prevMiddleVal - middleVal) > 1) {
            final double fixedMiddleVal = middleVal;

            double lowerSum = Arrays.stream(mappedCities).filter(v -> v <= fixedMiddleVal).sum();
            double higherSum = Arrays.stream(mappedCities).filter(v -> v > fixedMiddleVal).sum();

            prevMiddleVal = middleVal;

            if (lowerSum > higherSum) {
                // need to set it lower
                if (!wasToLow) {
                    upFactor /= 2;
                }
                middleVal /= 1.0 + upFactor;
                wasToLow = true;
            } else {
                // need to set it higher
                if (wasToLow) {
                    upFactor /= 2;
                }
                middleVal *= 1.0 + upFactor;
                wasToLow = false;
            }
        }

        return middleVal;
    }
}
