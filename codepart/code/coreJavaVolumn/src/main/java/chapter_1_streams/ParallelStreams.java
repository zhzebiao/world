package chapter_1_streams;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author zhzeb
 * @date 2020/5/23 21:51
 */
public class ParallelStreams {

    public static void main(String[] args) throws URISyntaxException, IOException {
        String contents = new String(Files.readAllBytes(
                Paths.get(OptionalTest.class.getClassLoader().getResource("pom.txt").toURI())),
                StandardCharsets.UTF_8
        );
        List<String> wordList = Arrays.asList(contents.split("\\PL+"));
        int[] shortWords = new int[10];
        wordList.parallelStream().forEach(
                s -> {
                    if (s.length() < 10) {
                        shortWords[s.length()]++;
                    }
                }
        );
        System.out.println(Arrays.toString(shortWords));

        Arrays.fill(shortWords, 0);

        wordList.parallelStream().forEach(
                s -> {
                    if (s.length() < 10) {
                        shortWords[s.length()]++;
                    }
                }
        );
        System.out.println(Arrays.toString(shortWords));

        Map<Integer, Long> shortWordCounts = wordList.parallelStream()
                .filter(s -> s.length() < 10)
                .collect(groupingBy(String::length, counting()));
        System.out.println("shortWordCounts" + shortWordCounts);

        ConcurrentMap<Integer, List<String>> result = wordList.parallelStream().collect(groupingByConcurrent(String::length));
        System.out.println(result.get(14));

        result = wordList.parallelStream().collect(groupingByConcurrent(String::length));
        System.out.println(result.get(14));

        ConcurrentMap<Integer, Long> wordCounts = wordList.parallelStream().collect(groupingByConcurrent(String::length, counting()));
        System.out.println(wordCounts);
    }
}