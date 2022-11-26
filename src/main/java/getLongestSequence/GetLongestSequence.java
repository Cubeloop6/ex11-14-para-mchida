package getLongestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */
    private static int CUTOFF;
    private static ForkJoinPool POOL = new ForkJoinPool();
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        SequenceRange result =  POOL.invoke(new LongestSequenceMask(val, arr, 0, arr.length));
        return result.longestRange;
    }

    /* TODO: Add a sequential method and parallel task here */

    static class LongestSequenceMask extends RecursiveTask<SequenceRange> {
        int low;
        int high;
        int arr[];
        int val;

        public LongestSequenceMask(int value, int[] array, int low, int high) {
            this.arr = array;
            this.val = value;
            this.low = low;
            this.high = high;

        }
        @Override
        protected SequenceRange compute() {
            if(high - low <= CUTOFF) {
                SequenceRange longest = longestSequence(val, arr, low, high);
                int longestVal = Math.max(longest.longestRange, Math.max(longest.matchingOnLeft, longest.matchingOnRight));
                return new SequenceRange(longest.matchingOnLeft, longest.matchingOnRight, longestVal,Math.min(high, arr.length) - low);
            }
            int mid = low + (high - low)/2;

            LongestSequenceMask left = new LongestSequenceMask(val, arr, low, mid);
            LongestSequenceMask right = new LongestSequenceMask(val, arr, mid, high);

            right.fork();

            SequenceRange leftVal = left.compute();
            SequenceRange rightValue = right.join();

            int finalLeft = leftVal.matchingOnLeft;
            int finalRight = rightValue.matchingOnRight;
            if(leftVal.matchingOnLeft == leftVal.sequenceLength) {
                finalLeft = leftVal.sequenceLength + rightValue.matchingOnLeft;
            }
            if(rightValue.matchingOnRight == rightValue.sequenceLength) {
                finalRight = rightValue.sequenceLength + leftVal.matchingOnRight;
            }
            int leftOrRight = Math.max(finalLeft, finalRight);
            int leftOrRight2 = Math.max(leftVal.longestRange, rightValue.longestRange);
            int maxVal = Math.max(leftOrRight, leftOrRight2);
            maxVal = Math.max(maxVal, leftVal.matchingOnRight + rightValue.matchingOnLeft);
            return new SequenceRange(finalLeft, finalRight, maxVal, rightValue.sequenceLength + leftVal.sequenceLength);
        }

    }
    public static SequenceRange longestSequence(int val, int[] arr, int lo, int hi) {
        int count = 0;
        int longestCount = 0;
        for(int i = lo; i < Math.min(hi, arr.length); i++) {
            if(arr[i] == val) {
                count++;
            } else {
                if(count > longestCount) {
                    longestCount = count;
                }
                count = 0;
            }
        }

        int left = 0;
        for(int i = lo; i < Math.min(hi, arr.length); i ++) {
            if(arr[i] != val) {
                break;
            } else {
                left++;
            }
        }

        int right = 0;
        for(int i = Math.min(hi - 1, arr.length); i >= lo; i--) {
            if(arr[i] != val) {
                break;
            } else {
                right++;
            }
        }
        return new SequenceRange(left, right, Math.max(longestCount, count), Math.min(hi, arr.length) - lo);

    }

    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
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
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}