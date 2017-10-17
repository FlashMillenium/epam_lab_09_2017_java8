package part1.exercise;

import java.util.Spliterators;
import java.util.function.IntConsumer;

public class RectangleSpliterator extends Spliterators.AbstractIntSpliterator {


    private final int innerLength;
    private final int[][] array;
    private final int startOuterInclusive;
    private final int endOuterExclusive;
    private int startInnerInclusive;

    public RectangleSpliterator(int[][] array) {
        this(array, 0, array.length, 0);
    }

    private RectangleSpliterator(int[][] array, int startOuterInclusive, int endOuterExclusive, int startInnerInclusive) {
        super(-1, 0);       // TODO заменить
//       super(estimatedSize, Spliterator.IMMUTABLE
//                          | Spliterator.ORDERED
//                          | Spliterator.SIZED
//                          | Spliterator.SUBSIZED
//                          | Spliterator.NONNULL);
        this.array = array;
    }

    @Override
    public OfInt trySplit() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public long estimateSize() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if(startOuterInclusive >= startInnerInclusive) {

            if (startInnerInclusive < innerLength) {
                int value = array[startOuterInclusive][startInnerInclusive++];
                action.accept(value);
                return true;
            }
        }
        throw new UnsupportedOperationException();
    }
}
