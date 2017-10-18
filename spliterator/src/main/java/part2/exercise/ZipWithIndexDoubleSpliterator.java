package part2.exercise;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class ZipWithIndexDoubleSpliterator extends Spliterators.AbstractSpliterator<IndexedDoublePair> {


    private final OfDouble inner;
    private int currentIndex;

    public ZipWithIndexDoubleSpliterator(OfDouble inner) {
        this(0, inner);
    }

    private ZipWithIndexDoubleSpliterator(int firstIndex, OfDouble inner) {
        super(inner.estimateSize(), inner.characteristics());
        currentIndex = firstIndex;
        this.inner = inner;
    }

    @Override
    public int characteristics() {
        return inner.characteristics();
    }

    @Override
    public boolean tryAdvance(Consumer<? super IndexedDoublePair> action) {
        boolean result = inner.tryAdvance((DoubleConsumer) e->
                action.accept(new IndexedDoublePair(currentIndex,e)));
        currentIndex++;
        return result;
    }

    @Override
    public void forEachRemaining(Consumer<? super IndexedDoublePair> action) {
        // TODO
        int i = currentIndex;
        boolean next;
        do{
            final int x = i;
            next=inner.tryAdvance((DoubleConsumer) e->
                    action.accept(new IndexedDoublePair(x,e)));
            i++;
        } while (next);
    }

    @Override
    public Spliterator<IndexedDoublePair> trySplit() {
        // TODO
        // if (inner.hasCharacteristics(???)) {
        //   use inner.trySplit
        // } else

        if (inner.hasCharacteristics(Spliterator.SIZED) && inner.hasCharacteristics(Spliterator.SUBSIZED)){
            OfDouble leftInner = inner.trySplit();
            if(Objects.isNull(leftInner)) return null;
            ZipWithIndexDoubleSpliterator result = new ZipWithIndexDoubleSpliterator(currentIndex,leftInner);
            currentIndex=(int)(currentIndex+leftInner.estimateSize());
            return result;
        }else
        {
            int ARRAY_SIZE = 128;
            double[] splitarray = new double[ARRAY_SIZE];
            for (int i = 0; i<splitarray.length; i++ ){
                int finalI = i;
                inner.tryAdvance((DoubleConsumer) (x)-> splitarray[finalI]=x);
            }
            ZipWithIndexDoubleSpliterator result = new ZipWithIndexDoubleSpliterator(currentIndex,Spliterators.spliterator(splitarray, splitarray.length));
            currentIndex += splitarray.length;
            return result;
        }
    }

    @Override
    public long estimateSize() {
        return inner.estimateSize();
    }
}
