package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */
    public static int[] filterEmpty(String[] arr) {
        int[] bits = mapToBitSet(arr);

        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);

        return mapToOutput(arr, bits, bitsum);
    }

    public static int[] mapToBitSet(String[] arr) {
        /* TODO: Edit this with your code */
        int[] bits1 = new int[arr.length];
        POOL.invoke(new BitTask(arr, bits1, 0, arr.length));
        return bits1;
    }

    /* TODO: Add a sequential method and parallel task here */
    static class BitTask extends RecursiveAction {
        int lo;
        int hi;
        String[] arr1;
        int[] bits1;

        public BitTask(String[] arr, int[] bits1, int lo, int high) {
            this.arr1 = arr;
            this.bits1 = bits1;
            this.lo = lo;
            this.hi = high;
        }

        @Override
        protected void compute() {
            if(hi - lo <= 1) {
                for (int i = lo; i < hi; i++) {
                    bits1[i] = arr1[i].length() >= 1 ? 1 : 0;
                }
                return;
            }

            int mid = lo + (hi - lo)/2;

            BitTask left = new BitTask(arr1, bits1, lo, mid);
            BitTask right = new BitTask(arr1, bits1, mid, hi);
            right.fork();
            left.compute();
            right.join();
        }
    }

    public static int[] mapToOutput(String[] input, int[] bits, int[] bitsum) {
        /* TODO: Edit this with your code */
        if (bitsum.length == 0) {
            return new int[0];
        }
        int length = bitsum[bitsum.length - 1];
        int[] result = new int[length];
        POOL.invoke(new OutputTask(input, result, bitsum, bits, 0, input.length));
        return result;
    }

    /* TODO: Add a sequential method and parallel task here */

    public static class OutputTask extends RecursiveAction {
        int[] bitset;
        int[] bitsum;
        String[] arr2;
        int[] result;
        int lo;
        int high;

        public OutputTask(String[] input, int[] output, int[] bitsum, int[] bitset, int lo, int hi ) {
            this.arr2 = input;
            this.bitsum = bitsum;
            this.bitset = bitset;
            this.result = output;
            this.lo = lo;
            this.high = hi;
        }
        @Override
        protected void compute() {
            if(high - lo <= 1) {
                for(int i = lo; i < high; i ++) {
                    if(bitset[i] == 1) {
                        result[bitsum[i] - 1] = arr2[i].length();
                    }
                }
                return ;
            }

            int mid = lo + (high - lo)/2;
            OutputTask left = new OutputTask(arr2, result, bitsum, bitset, lo, mid);
            OutputTask right = new OutputTask(arr2, result, bitsum, bitset, mid, high);
            left.fork();
            right.compute();
            left.join();

        }

    }


    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}