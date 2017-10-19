package part1.exercise;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;

public class RectangleSpliterator extends Spliterators.AbstractIntSpliterator {

    private final int[][] array;
    private int startOuterInclusive;
    private final int endOuterExclusive;
    private final int innerLength;
    private int startInnerInclusive;
    private int lastRowEndIndexExclusive;
    int index=0;

    public RectangleSpliterator(int[][] array) {
        this(array, 0, array.length, 0, 0);
    }

    private RectangleSpliterator(int[][] array, int startOuterInclusive, int endOuterExclusive, int startInnerInclusive, int lastRowEndIndexExclusive) {
        super(array.length==0 ? 0 : array[0].length==0 ? 0 : array.length*array[0].length,
                Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE);
        this.startOuterInclusive=startOuterInclusive;
        this.endOuterExclusive=endOuterExclusive;
        this.startInnerInclusive=startInnerInclusive;
        this.innerLength=array[0].length;
        this.lastRowEndIndexExclusive = lastRowEndIndexExclusive==0?array[0].length:lastRowEndIndexExclusive;
        this.array = array;
    }

    @Override
    public OfInt trySplit() {
        long size =estimateSize();
        if(size<10) return null;
        System.out.println(size);
//       simple split by rows
//        int length =(endOuterExclusive-startOuterInclusive)/2;
//       if (length<2)
//           return null;
//       int mid = startOuterInclusive + length;
//        System.out.println(mid+" " +endOuterExclusive+" "+startOuterInclusive);
//        RectangleSpliterator result = new RectangleSpliterator(array, startOuterInclusive,
//                    mid, startInnerInclusive, 0);
//        this.startOuterInclusive=mid;
//        return result;

        // i'm tryharder
        int halfsize=(int)size/2;
        int borderShift = innerLength==0? innerLength : innerLength - startInnerInclusive;
        if((halfsize- borderShift)<1) return null;  // i have this state when array is [7][7]
        int newEndIndex =(halfsize- borderShift)%innerLength; // this index not on same row so it's like startInnerInclusive =0
        int newRowIndex =startOuterInclusive+halfsize/innerLength;
        if(startOuterInclusive==newRowIndex) //if we have only one row
            if(newEndIndex<startInnerInclusive) //and indexes don't fucked up
                return null;
            else
                 newEndIndex = startInnerInclusive + (halfsize- borderShift)%innerLength; //new index in same row calc
        RectangleSpliterator result =new RectangleSpliterator(array, startOuterInclusive,
              newEndIndex==0?newRowIndex : newRowIndex+1, // newRowIndex is exclusive parameter, so if we need use item
                                                   // from this row in new spliterator we need add +1 to this parameter
                startInnerInclusive, newEndIndex);
        this.startOuterInclusive = newRowIndex;
        this.startInnerInclusive= newEndIndex;
        return result;
    }

    @Override
    public long estimateSize() {
        return (endOuterExclusive-startOuterInclusive-1)*innerLength-startInnerInclusive+lastRowEndIndexExclusive;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if(endOuterExclusive > startOuterInclusive) {
            int border = endOuterExclusive-startOuterInclusive==1?lastRowEndIndexExclusive:innerLength;
            if (startInnerInclusive < border) {
                int value = array[startOuterInclusive][startInnerInclusive++];
                action.accept(value);
                return true;
            }
            else {
                startOuterInclusive++;
                startInnerInclusive=0;
                return true;
            }
        }
        return false;
    }
}
