package part1.exercise;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@State(Scope.Thread)
public class RectangleSpliteratorExercise {

    @Param({"100"})
    public int outerLength;

    @Param({"100"})
    public int innerLength;

    private int[][] array;

    @Setup
    public void setup() {
        array = new int[outerLength][];

        for (int i = 0; i < array.length; i++) {
            int[] inner = new int[innerLength];
            array[i] = inner;
            for (int j = 0; j < inner.length; j++) {
                inner[j] = ThreadLocalRandom.current().nextInt();
            }
        }
    }


    @Benchmark
    public long baselineSequential() {
        return Arrays.stream(array)
                     .sequential()
                     .flatMapToInt(Arrays::stream)
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long baselineParallel() {
        return Arrays.stream(array)
                     .parallel()
                     .flatMapToInt(Arrays::stream)
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long rectangleSequential() {
        return StreamSupport.intStream(new RectangleSpliterator(array), false)
                            .asLongStream()
                            .sum();
    }

    @Benchmark
    public long rectangleParallel() {
        return StreamSupport.intStream(new RectangleSpliterator(array), true)
                            .asLongStream()
                            .sum();
    }
}
