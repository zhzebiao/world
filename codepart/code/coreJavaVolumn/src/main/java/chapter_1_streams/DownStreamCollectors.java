package chapter_1_streams;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author zhzeb
 * @date 2020/5/23 20:13
 */
public class DownStreamCollectors {

    public static class City {
        private String name;
        private String state;
        private int population;

        public City(String name, String state, int population) {
            this.name = name;
            this.state = state;
            this.population = population;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getPopulation() {
            return population;
        }

        public void setPopulation(int population) {
            this.population = population;
        }
    }

    public static Stream<City> readCities(String filename) throws IOException, URISyntaxException {
        return Files.lines(Paths.get(DownStreamCollectors.class.getClassLoader().getResource(filename).toURI()))
                .map(l -> l.split("\\s+"))
                .map(a -> new City(a[0], a[1], Integer.parseInt(a[2])));
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<Locale>> countryToLocaleSet = locales.collect(groupingBy(Locale::getCountry, toSet()));
        System.out.println("countryToLocaleSet: " + countryToLocaleSet);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Long> countryToLocaleCounts = locales.collect(groupingBy(Locale::getCountry, counting()));
        System.out.println("countryToLocaleCounts" + countryToLocaleCounts);

        Stream<City> cities = readCities("cities.txt");
        Map<String, Integer> stateToCityPopulation = cities.collect(groupingBy(City::getState, summingInt(City::getPopulation)));
        System.out.println("stateToCityPopulation" + stateToCityPopulation);

        cities = readCities("cities.txt");
        Map<String, Optional<String>> stateToLongestCityName = cities.collect(groupingBy(City::getState, mapping(
                City::getName, maxBy(Comparator.comparing(String::length))
        )));
        System.out.println("stateToLongestCityName" + stateToLongestCityName);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<String>> countryToLanguage = locales.collect(groupingBy(Locale::getDisplayCountry, mapping(
                Locale::getDisplayLanguage, toSet()
        )));
        System.out.println("countryToLanguage" + countryToLanguage);

        cities = readCities("cities.txt");
        Map<String, IntSummaryStatistics> stateToCityPopulationSummary = cities.collect(groupingBy(City::getState, summarizingInt(City::getPopulation)));
        System.out.println("stateToCityPopulationSummary" + stateToCityPopulationSummary);

        cities = readCities("cities.txt");
        Map<String, String> stateToCityNames = cities.collect(groupingBy(City::getState, reducing(
                "1234", City::getName, (s, t) -> s.length() == 0 ? t : s + ", " + t
        )));
        System.out.println("stateToCityNames" + stateToCityNames);

        cities = readCities("cities.txt");
        stateToCityNames = cities.collect(groupingBy(City::getState,
                mapping(City::getName, joining(", "))));
        System.out.println("stateToCityNames" + stateToCityNames);
    }
}