package chapter_1_streams;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhzeb
 * @date 2020/5/19 22:18
 */
public class CollectingResults {

    public static Stream<String> noVowels() throws URISyntaxException, IOException {
        String contents = new String(Files.readAllBytes(
                Paths.get(OptionalTest.class.getClassLoader().getResource("pom.txt").toURI())),
                StandardCharsets.UTF_8
        );
        List<String> wordList = Arrays.asList(contents.split("\\PL+"));
        Stream<String> words = wordList.stream();
        return words.map(s -> s.replaceAll("[aeiouAEIOU]", ""));
    }

    public static <T> void show(String label, Set<T> set) {
        System.out.println(label + ": " + set.getClass().getName());
        System.out.println("["
                + set.stream().limit(10).map(Object::toString).collect(Collectors.joining(", "))
                + "]"
        );
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        Iterator<Integer> iter = Stream.iterate(0, n -> n + 1).limit(10).iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }

        Object[] numbers = Stream.iterate(0, n -> n + 1).limit(10).toArray();
        System.out.println("Object array: " + numbers);

        try {
            Integer number = (Integer) numbers[0];
            System.out.println("number:" + number);
            System.out.println("The following statment throws an execption: ");
            Integer[] numbers2 = (Integer[]) numbers;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Integer[] numbers3 = Stream.iterate(0, n -> n + 1)
                .limit(10)
                .toArray(Integer[]::new);
        System.out.println("Integer array:" + numbers3);

        TreeSet<String> noVowelTreeSet = noVowels().collect(
                Collectors.toCollection(TreeSet::new)
        );
        show("noVowelTreeSet", noVowelTreeSet);

        String result = noVowels().limit(10).collect(Collectors.joining());
        System.out.println("Joining: " + result);
        result = noVowels().limit(10).collect(Collectors.joining(", "));
        System.out.println("Joining with commas: " + result);

        IntSummaryStatistics summary = noVowels().collect(Collectors.summarizingInt(String::length));
        double average = summary.getAverage();
        int max = summary.getMax();
        System.out.println("Average word length: " + average);
        System.out.println("Max word length: " + max);
        System.out.println("forEach:");
        noVowels().forEach(System.out::println);

        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<String>> countryLanguageSets = locales.collect(
                Collectors.toMap(
                        Locale::getDisplayCountry,
                        l -> Collections.singleton(l.getDisplayLanguage()),
                        (a, b) -> {
                            Set<String> union = new HashSet<>(a);
                            union.addAll(b);
                            return union;
                        }
                )
        );
        System.out.println("countryLanguageSets: "+ countryLanguageSets);

        Stream<String> emptyStream = Stream.empty();
        System.out.println(emptyStream.collect(Collectors.summarizingInt(String::length)).getMax());
    }
}