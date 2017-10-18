package part1.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@State(Scope.Thread)
public class RandomIntegers {

    @Param({"100000"})
    public int count;

    @Param({"0", "10", "100", "1000"})
    public long consume;

    private int getAnInt() {
        Blackhole.consumeCPU(consume);
        return ThreadLocalRandom.current().nextInt();
    }

    public Iterator<Integer> createIterator() {
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                return getAnInt();
            }
        };
    }

    @Benchmark
    public long classic() {
        Iterator<Integer> iterator = createIterator();
        long result = 0;
        for (int i = 0; i < count; i++) {
            result += iterator.next();
        }
        return result;
    }

    @Benchmark
    public long fromIteratorSequential() {
        Iterator<Integer> iterator = createIterator();
        Spliterator<Integer> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false)
                            .limit(count)
                            .mapToInt(i -> i)
                            .asLongStream()
                            .sum();
    }

    @Benchmark
    public long fromIteratorParallel() {
        Iterator<Integer> iterator = createIterator();
        Spliterator<Integer> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, true)
                            .limit(count)
                            .mapToInt(i -> i)
                            .asLongStream()
                            .sum();
    }

    @Benchmark
    public long generateSequential() {
        return Stream.generate(this::getAnInt)
                     .sequential()
                     .limit(count)
                     .mapToInt(i -> i)
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long generateParallel() {
        return Stream.generate(this::getAnInt)
                     .parallel()
                     .limit(count)
                     .mapToInt(i -> i)
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long spliteratorSequential() {
        return StreamSupport.stream(new RandomIntegerSpliterator(), false)
                            .limit(count)
                            .mapToInt(i -> i)
                            .asLongStream()
                            .sum();
    }

    @Benchmark
    public long spliteratorParallel() {
        return StreamSupport.stream(new RandomIntegerSpliterator(), true)
                            .limit(count)
                            .mapToInt(i -> i)
                            .asLongStream()
                            .sum();
    }

    private class RandomIntegerSpliterator extends Spliterators.AbstractSpliterator<Integer> {

        private long estimation;

        private RandomIntegerSpliterator(long estimation) {
            super(estimation, (Spliterator.NONNULL | Spliterator.IMMUTABLE));
            this.estimation = estimation;
        }

        public RandomIntegerSpliterator() {
            this(Long.MAX_VALUE);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Integer> action) {
            action.accept(getAnInt());
            return true;
        }

        @Override
        public Spliterator<Integer> trySplit() {
            long estimation = this.estimation / 2;
            return estimation == 0 ? null : new RandomIntegerSpliterator(estimation);
        }

        @Override
        public long estimateSize() {
            return estimation;
        }
    }
}
