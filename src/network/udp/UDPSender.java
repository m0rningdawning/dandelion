package network.udp;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSender {
    private final int PORT = 9999;
    private static DatagramSocket socket;
    private static int packetsCount = 1000;
    private static int threadCount = 10;
    private static int delay = 1;
    private static int timeout = 10;

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
                    e.printStackTrace();
                }
            });
            inputThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeThreadPool() {
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++)
                executor.execute(new UDPSenderTask(socket, packetsCount, delay, timeout));
        }
    }

    public void stop() {
        if (socket != null && !socket.isClosed())
            socket.close();
        System.out.println("Stopped! Exiting...");
    }
}

