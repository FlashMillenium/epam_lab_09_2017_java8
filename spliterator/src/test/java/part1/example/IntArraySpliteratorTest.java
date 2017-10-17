package part1.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class IntArraySpliteratorTest {

    private int[] arr;

    @BeforeEach
    void setup() {
        arr = IntStream.generate(() -> 1).limit(100).toArray();
    }

    @Test
    void testParallelSeq() {
        assertEquals(100, StreamSupport.intStream(new IntArraySpliterator(arr), false).sum());
    }

    @Test
    void testParallelStream() {
        assertEquals(100, StreamSupport.intStream(new IntArraySpliterator(arr), true).sum());
    }

    @Test
    void testMaxMethodOfSteam() {
        assertEquals(50, StreamSupport.intStream(new IntArraySpliterator(new int[]{1, 50, 10}), true)
                                              .max()
                                              .orElseThrow(IllegalStateException::new));
    }

}