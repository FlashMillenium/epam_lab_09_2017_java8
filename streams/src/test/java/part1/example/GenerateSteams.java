package part1.example;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenerateSteams {

    public static void main(String[] args) {
        Stream<Integer> stream1 = Arrays.asList(1, 2, 3).stream();
        Stream<Boolean> booleanStream = Stream.of(true, false, false, true);
//            Files.line
        String string = "abcde";
        string.chars();
        Stream<Integer> generate = Stream.generate(() -> 1);
        Stream<Integer> iterate = Stream.iterate(0, integer -> integer + 2);
        IntStream ints = new Random().ints(20, 0, 100);
        IntStream stream = Arrays.stream(new int[] {1, 2, 3});
    }
}
