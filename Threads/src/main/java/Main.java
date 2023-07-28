import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
                for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        List<Future<Integer>> tasks = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(25);

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            Future<Integer> task = threadPool.submit(() -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            });
            tasks.add(task);
        }

        Integer[] maximum = new Integer[texts.length];
        for (int i = 0; i < maximum.length; i++) {
            maximum[i] = tasks.get(i).get();
        }

        threadPool.shutdown();

        int max = 0;
        int number = 0;

        for(int i = 0; i < maximum.length; i++) {
            if (maximum[i] > max) {
                max = maximum[i];
                number = i;
            }
        }

        System.out.println("Максимальное число подряд идущих 'a' - " + max + " в строке: " + number);

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}