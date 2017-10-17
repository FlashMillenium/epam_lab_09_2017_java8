package part1.example.sum;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
public class JMHBenchmark {

    @Benchmark
    public long simple() {
        return sumTwice(10_000_000);
    }

    @Benchmark
    public long stream() {
        return sumTwiceStream(10_000_000);
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
