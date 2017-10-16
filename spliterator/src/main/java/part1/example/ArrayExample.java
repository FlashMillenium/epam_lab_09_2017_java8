package part1.example;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;

//https://github.com/java8-course/spliterator.git

public class ArrayExample {
    public static class IntArraySpliterator extends Spliterators.AbstractIntSpliterator {

        private final int[] array;
        private int startInclusive;
        private final int endExclusive;

        public IntArraySpliterator(int[] array) {
            this(array, 0, array.length);
        }

        private IntArraySpliterator(int[] array, int startInclusive, int endExclusive) {
            super(endExclusive - startInclusive,
                    Spliterator.IMMUTABLE
                            | Spliterator.ORDERED
                            | Spliterator.SIZED
                            | Spliterator.SUBSIZED
                            | Spliterator.NONNULL);
            this.array = array;
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            return false;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public OfInt trySplit() {
            return null;
        }
    }
}
