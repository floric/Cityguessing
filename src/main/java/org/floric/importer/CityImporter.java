package org.floric.importer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.floric.model.City;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by florian on 4/13/17.
 */
public class CityImporter {

    private Map<String, String> stateMapping = Maps.newHashMap();

    public CityImporter() {
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
    }

    public Set<City> importFromS3(String bucketName, String fileName) throws IOException {
        AmazonS3 client = new AmazonS3Client();

        if (!client.doesBucketExist(bucketName)) {
            throw new FileNotFoundException("S3 Bucket not found!");
        }

        S3Object countryFile = client.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = countryFile.getObjectContent();

        return importCsvFile(objectContent);
    }

    public Set<City> importFromLocalFolder(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() || !f.isFile()) {
            throw new RuntimeException("Invalid file!");
        }

        return importCsvFile(new FileInputStream(f));
    }

    private Set<City> importCsvFile(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(stream));

        return br.lines()
                .filter(this::isCity)
                .map(this::mapToCity)
                .filter(this::isValidCity)
                .collect(Collectors.toSet());
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
        ArrayList<String> allowedTypes = Lists.newArrayList("PPLA", "PPLA2", "PPLA3", "PPLA4", "PPLC", "PPL");
        int population = Integer.parseInt(split[14]);
        return Objects.equals(split[6], "P") && allowedTypes.contains(split[7]) && population > 0;
    }

    private boolean isValidCity(City c) {
        return c.getPopulation() > 0;
    }
}
