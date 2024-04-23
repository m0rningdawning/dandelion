package network.udp;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPSender {
    private static final int PORT = 9999;
    private static DatagramSocket socket;
    private static int packetsCount = 100000;
    private static int threadCount = 50;
    private static int delay = 1;
    private static int timeout = 30;

    private static ExecutorService executor;

    private static final Logger logger = Logger.getLogger(UDPSender.class.getName());

    public UDPSender() {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("UDP port " + PORT);

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
                        logger.log(Level.SEVERE, "Error within the UDPSender main thread ", e);
                    }
                });

                inputThread.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error within the UDPSender constructor ", e);
        }
    }

    private static void initializeThreadPool() {
        executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++)
            executor.execute(new UDPSenderTask(socket, PORT, packetsCount, delay, timeout));

        executor.shutdown();
    }



    public void stop() {
        if (executor != null)
            executor.shutdownNow();
        if (socket != null && !socket.isClosed())
            socket.close();
        System.out.println("Stopped! Exiting...");
    }
}

