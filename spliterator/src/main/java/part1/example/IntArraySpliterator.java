package part1.example;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;

public class IntArraySpliterator extends Spliterators.AbstractIntSpliterator {

    private final int[] array;
    private final int endExclusive;
    private int startInclusive;

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
        if (startInclusive < endExclusive) {
            int value = array[startInclusive];
            startInclusive += 1;
            action.accept(value);
            return true;
        }
        return false;
    }

    @Override
    public OfInt trySplit() {
        int length = endExclusive - startInclusive;
        if (length < 2) {
            return null;
        }

        int mid = startInclusive + length / 2;
        IntArraySpliterator result = new IntArraySpliterator(array, startInclusive, mid);
        startInclusive = mid;
        return result;
    }

    @Override
    public long estimateSize() {
        return endExclusive - startInclusive;
    }

    @Override
    public void forEachRemaining(IntConsumer action) {
        for (int i = startInclusive; i < endExclusive; i++) {
            action.accept(array[i]);
        }
        startInclusive = endExclusive;
    }
}