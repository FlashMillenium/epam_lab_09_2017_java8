package part1.example.sum;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NaiveBenchmark {

    public static void main(String[] args) {
        long startSimple = System.nanoTime();
        long resultSimple = sumTwice(10_000_000);
        long endSimple = System.nanoTime();
        System.out.printf("Simple: %d; time=%8.3fms%n", resultSimple, (double)TimeUnit.NANOSECONDS.toMillis(endSimple - startSimple));

        long startStream = System.nanoTime();
        long resultStream = sumTwiceStream(10_000_000);
        long endStream = System.nanoTime();
        System.out.printf("Stream: %d; time=%8.3fms%n", resultStream, (endStream - startStream) / 1_000_000.0);
    }

    private static long sumTwice(int threshold) {
        long sum = 0;
        for (int i = 0; i <= threshold; ++i) {
            sum += i * 2;
        }
        return sum;
    }

    private static long sumTwiceStream(int threshold) {
        return IntStream.rangeClosed(1, threshold)
                        .mapToLong(x -> x * 2)
                        .sum();
    }
}
