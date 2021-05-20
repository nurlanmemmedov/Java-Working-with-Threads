import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitThread {
    public static Object lockObject = new Object();

    public static void main(String[] args) throws IOException, InterruptedException {
        int k = 10;
        int javaDefaultThreadCount = Thread.activeCount();
        List<String> lines = new ArrayList<>();
        AtomicInteger threadCount = new AtomicInteger();

        BufferedReader bufferedReader = Files.newBufferedReader(Path.of("sample.txt"));
        String line = null;
        int lineCount = 0;

        while((line = bufferedReader.readLine()) != null) {
            if (lineCount == 10) {
                List<String> linesForThread = new ArrayList<>();
                for (String l : lines){
                    linesForThread.add(l);
                }

                threadCount.getAndIncrement();
                new Thread(() -> {
                    for (String ln : linesForThread){
                        System.out.println(findWordCount(ln));
                    }
                    System.out.println("My thread is ------ "+ Thread.currentThread().getName());
                    System.out.println("Active threads are --------- "+(Thread.activeCount()-javaDefaultThreadCount));
                    synchronized (lockObject){
                        lockObject.notify();
                    }
                    threadCount.getAndDecrement();
                }).start();

                lineCount = 0;
                lines.removeAll(lines);
            }


            synchronized( lockObject )
            {
                while (threadCount.get() >= k )
                {
                    lockObject.wait();
                }
            }

            lines.add(line);
            lineCount++;
        }
    }

    public static int findWordCount(String line) {
        return line.split("\\s").length;
    }
}
