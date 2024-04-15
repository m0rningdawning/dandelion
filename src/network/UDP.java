package network;

import java.io.*;
import java.net.*;

public class UDP {
    private final int SERVER_PORT = 9999;
    private static DatagramSocket socket;

    public UDP() {
        try {
            socket = new DatagramSocket(SERVER_PORT);
            System.out.println("UDP port " + SERVER_PORT);

            Thread inputThread = new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    start();
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

    private static void start() {
        Thread senderThread = new Thread(() -> {
            try {
                // Everything local for now
                InetAddress targetAddress = InetAddress.getLocalHost();
                int targetPort = 8888;

                for (int i = 0; i < 10; i++) {
                    String message = "Packet " + i;
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
                    socket.send(sendPacket);
                    System.out.println("Sent to client: " + message);
                    Thread.sleep(500); // Just to be safe XD
                }
                System.out.println("Finished! Press 'q' to exit.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Within \"start\" method, UDP.java: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
            }
        });
        senderThread.start();
    }

    public void stop() {
        if (socket != null && !socket.isClosed())
            socket.close();
        System.out.println("Stopped! Exiting...");
    }
}

