package network.udp;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPSender {
    private final int startingPort;
    private final int targetPort;
    private final int packetsCount;
    private final int threadCount;
    private final int timeout;

    private static ExecutorService executor;
    private static CountDownLatch latch;
    private static final Logger logger = Logger.getLogger(UDPSender.class.getName());

    public UDPSender(int startingPort, int targetPort, int packetsCount, int threadCount, int timeout) {
        this.startingPort = startingPort;
        this.targetPort = targetPort;
        this.packetsCount = packetsCount;
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
                        logger.log(Level.SEVERE, "Error within the UDPSender main thread ", e);
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
                logger.log(Level.SEVERE, "Error within the UDPSender main thread ", e);
            }
        });

        inputThread.start();
    }

    private void initializeThreadPool() throws SocketException {
        executor = Executors.newFixedThreadPool(threadCount);
        latch = new CountDownLatch(threadCount);

        // For every thread within the thread pool initialize a sender task
        // DatagramSocket within each of the tasks will be opened on a port = startingPort + nOfThread
        for (int i = 0; i < threadCount; i++)
            executor.execute(new UDPSenderTask(i + 1, startingPort + i, targetPort, packetsCount, timeout, latch));

        executor.shutdown();
    }

    private void printFinish(){
        System.out.println("UDP Attack finished!");
    }

    public void stop() {
        if (executor != null)
            executor.shutdownNow();
        System.out.println("Stopped! Exiting...");
    }
}

