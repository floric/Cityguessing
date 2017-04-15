package org.floric.importer;

import com.google.common.collect.Maps;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.floric.model.City;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/13/17.
 */
public class CityImporter {

    private Map<String, String> stateMapping = Maps.newHashMap();

    public List<City> importCsvFile(String path) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        stateMapping.put("01", "Baden-Württemberg");
        stateMapping.put("02", "Bayern");
        stateMapping.put("03", "Bremen");
        stateMapping.put("04", "Hamburg");
        stateMapping.put("05", "Hessen");
        stateMapping.put("06", "Niedersachsen");
        stateMapping.put("07", "Nordrhein-Westfalen");
        stateMapping.put("08", "Rheinland-Pfalz");
        stateMapping.put("09", "Saarland");
        stateMapping.put("10", "Schleswig-Holstein");
        stateMapping.put("11", "Brandenburg");
        stateMapping.put("12", "Mecklemburg-Vorpommern");
        stateMapping.put("13", "Sachsen");
        stateMapping.put("14", "Sachsen-Anhalt");
        stateMapping.put("15", "Thüringen");
        stateMapping.put("16", "Berlin");

        return br.lines()
                .filter(this::isCity)
                .map(this::mapToCity)
                .filter(this::isValidCity)
                .collect(Collectors.toList());
    }

    private City mapToCity(String line) {
        String[] split = line.split("\t");

        String cityName = split[1];
        Vector2D coordinate = new Vector2D(Double.parseDouble(split[4]), Double.parseDouble(split[5]));
        double altitude = 1.0;
        if (split[14].isEmpty()) {
            throw new RuntimeException("Invalid population for " + cityName);
        }

        int population = Integer.parseInt(split[14]);
        List<String> alternateNames = Arrays.stream(split[3].split(",")).collect(Collectors.toList());
        String countryCode = split[8];
        String stateCode = stateMapping.get(split[10]);
        if (stateCode == null) {
            throw new RuntimeException("Unknown state for " + cityName);
        }

        return new City(cityName, coordinate, altitude, population, alternateNames, countryCode, stateCode);
    }

    private boolean isCity(String line) {
        String[] split = line.split("\t");
        return Objects.equals(split[6], "P");
    }

    private boolean isValidCity(City c) {
        return c.getPopulation() > 0;
    }
}
