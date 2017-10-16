package forkjoinpool;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FJPExample {


    public static final int SIZE = 1_000_000;

    public static void main(String[] args) {
        int[] arr = new int[SIZE];
        Arrays.fill(arr, 1);

        Integer result = new ForkJoinPool().invoke(new SumTask(arr, 0, arr.length));
        System.out.println(result);
    }

    private static class SumTask extends RecursiveTask<Integer> {

        private int[] data;
        private int from;
        private int to;

        public SumTask(int[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Integer compute() {
            if (to - from < 100_000) {
                int result = 0;
                for (int i = from; i < to; i++) {
                    result += data[i];
                }
                return result;
            }
            SumTask left = new SumTask(data, from, (to + from) / 2);
            SumTask right = new SumTask(data, (to + from) / 2 + 1, to);

            ForkJoinTask<Integer> fork1 = left.fork();
            ForkJoinTask<Integer> fork2 = right.fork();

            return fork1.join() + fork2.join();
        }
    }
}
