package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HasOver {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns true if arr has any elements strictly larger than val.
     * For example, if arr is [21, 17, 35, 8, 17, 1], then
     * main.java.hasOver(21, arr) == true and main.java.hasOver(35, arr) == false.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument.
     */
    private static int CUTOFF;
    private static ForkJoinPool POOL = new ForkJoinPool();
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new HasOverTask(val, arr, 0, arr.length));
    }

    /* TODO: Add a sequential method and parallel task here */
    static class HasOverTask extends RecursiveTask<Boolean> {
        int lo;
        int hi;
        int[] arr;
        int val;
        public HasOverTask(int val, int[] arr, int lo, int high) {
            this.lo = lo;
            this.hi = high;
            this.arr = arr;
            this.val = val;
        }

        @Override
        protected Boolean compute() {
            if(hi - lo <= CUTOFF) {
                if(Max(arr, lo, hi) > val) {
                    return true;
                } else {
                    return false;
                }
            }
            int mid = lo + (hi - lo)/2;
            HasOverTask left = new HasOverTask(val, arr, lo, mid);
            HasOverTask right = new HasOverTask(val, arr, mid, hi);

            right.fork();

            boolean leftAns = left.compute();
            boolean rightAns = right.join();

            return leftAns || rightAns;
        }

    }
    private static int Max(int[]arr, int lo, int hi) {
        int maxVal = arr[lo];
        for(int i = lo + 1; i < hi; i++) {
            if(arr[i] > maxVal) {
                maxVal = arr[i];
            }
        }
        return maxVal;
    }
    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }
}
