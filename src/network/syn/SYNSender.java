package network.syn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SYNSender {
    private final int port;
    private final int targetPort;
    private final int packetsCount;
    private final int threadCount;
    private final int timeout;

    private static ExecutorService executor;

    private static final Logger logger = Logger.getLogger(SYNSender.class.getName());

    public SYNSender(int port, int targetPort, int packetsCount, int threadCount, int timeout) {
        this.port = port;
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

        for (int i = 0; i < threadCount; i++)
            executor.execute(new SYNSenderTask());

        executor.shutdown();
    }


    public void stop() {
        if (executor != null)
            executor.shutdownNow();
        System.out.println("Stopped! Exiting...");
    }
}
