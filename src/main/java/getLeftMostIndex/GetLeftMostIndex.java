package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */
    private static ForkJoinPool POOL  = new ForkJoinPool();
    private static int CUTOFF;
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new LeftMostIndexTask(needle, haystack, 0, haystack.length));
    }

    /* TODO: Add a sequential method and parallel task here */
    static class LeftMostIndexTask extends RecursiveTask<Integer> {
        char[] needle;
        char[] haystack;
        int lo;
        int high;
        public LeftMostIndexTask(char[] needle, char[] haystack, int low, int hi) {
            this.lo = low;
            this.high = hi;
            this.needle = needle;
            this.haystack = haystack;
        }
        @Override
        protected Integer compute() {
            if(high - lo <= CUTOFF) {
                for(int i = lo; i < high; i ++) {
                    if(i + needle.length <= haystack.length) {
                        boolean found = true;
                        for(int j = 0; j < needle.length; j++) {
                            if(haystack[i + j] != needle[j]) {
                                found = false;
                                break;
                            }
                        }

                        if(found) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            int mid = lo + (high - lo)/2;
            LeftMostIndexTask left = new LeftMostIndexTask(needle, haystack, lo, mid);
            LeftMostIndexTask right = new LeftMostIndexTask(needle, haystack, mid, high);
            right.fork();

            int result = left.compute();
            int result2 = right.join();

            if(result == -1) {
                return result2;
            } else {
                return result;
            }
        }


    }
    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
