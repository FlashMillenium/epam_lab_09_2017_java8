package part1.example;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CollectorsExample {

    public static void main(String[] args) {
        IntStream range = IntStream.range(0, 100);
        // "0" -> 0
        // "1" -> 1
        // ...
        // "99" -> 99
        Map<String, Integer> collect = range.boxed().collect(Collectors.toMap(String::valueOf, Integer::valueOf));
        Map<String, Integer> collect2 = Stream.of(1, 2, 3, 3, 5).collect(Collectors.toMap(String::valueOf, Integer::valueOf, (oldValue, newValue) -> oldValue + newValue));
        Map<String, Integer> collect3 = Stream.of(1, 2, 3, 3, 5).collect(Collectors.toMap(String::valueOf, Integer::valueOf, (oldValue, newValue) -> oldValue + newValue, TreeMap::new));

        List<Integer> collect1 = range.boxed().collect(Collectors.toList());

        Map<Boolean, List<Integer>> collect4 = range.boxed().collect(Collectors.groupingBy(intValue -> intValue > 50));
        Map<Boolean, List<Integer>> collect5 = range.boxed().collect(Collectors.partitioningBy(o -> o > 50));
        Integer collect6 = range.boxed().collect(Collectors.summingInt(value -> value));
        IntSummaryStatistics collect7 = range.boxed().collect(Collectors.summarizingInt(value -> value));
        String collect8 = Stream.of("a", "b", "c").collect(Collectors.joining());
           String collect9 = Stream.of("a", "b", "c").collect(Collectors.joining(" "));
        String collect10 = Stream.of("a", "b", "c").collect(Collectors.joining(" ", "->", "<-"));
    }
}
