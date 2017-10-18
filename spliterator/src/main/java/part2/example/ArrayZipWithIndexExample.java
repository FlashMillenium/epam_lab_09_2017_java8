package part2.example;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class ArrayZipWithIndexExample {

    public static class IndexedArraySpliterator<T> extends Spliterators.AbstractSpliterator<IndexedPair<T>> {


        private final T[] array;
        private int startInclusive;
        private final int endExclusive;

        public IndexedArraySpliterator(T[] array) {
            this(array, 0, array.length);
        }

        private IndexedArraySpliterator(T[] array, int startInclusive, int endExclusive) {
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
        public boolean tryAdvance(Consumer<? super IndexedPair<T>> action) {
            if (startInclusive < endExclusive) {
                action.accept(new IndexedPair<>(startInclusive, array[startInclusive]));
                startInclusive += 1;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super IndexedPair<T>> action) {
            for (int i = startInclusive; i < endExclusive; i++) {
                action.accept(new IndexedPair<>(i, array[i]));
            }
            startInclusive = endExclusive;
        }

        @Override
        public long estimateSize() {
            return endExclusive - startInclusive;
        }

        @Override
        public IndexedArraySpliterator<T> trySplit() {
            int length = endExclusive - startInclusive;
            if (length <= 1) {
                return null;
            }

            int middle = startInclusive + length/2;

            final IndexedArraySpliterator<T> newSpliterator = new IndexedArraySpliterator<>(array, startInclusive, middle);

            startInclusive = middle;

            return newSpliterator;
        }
    }
}
