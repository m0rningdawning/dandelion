package network.tcp;

import network.udp.UDPSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPSender {
    private final int TARGET_PORT = 8888;
    public static Socket socket;

    private static final Logger logger = Logger.getLogger(TCPSender.class.getName());

    public TCPSender() {
        try {
            InetAddress targetAddress = InetAddress.getLocalHost();
            socket = new Socket(targetAddress, TARGET_PORT);
            System.out.println("TCP port " + TARGET_PORT);

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
                    logger.log(Level.SEVERE, "Error within the TCPSender main thread ", e);
                }
            });
            inputThread.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error within the TCPSender constructor ", e);
        }
    }

    private static void start() {
        Thread senderThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
                    String data = "Another package";
                    outToServer.println(data);

                    System.out.println("Packet " + i);
                }
                System.out.println("Finished! Press 'q' to exit.");
            } catch (IOException e) {
                System.out.println("Within \"start\" method, TCP.java: " + e.getMessage());
                logger.log(Level.SEVERE, "Error within the TCPSender \"start()\" thread ", e);
                System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
            }
        });
        senderThread.start();
    }

    public void stop() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Stopped! Exiting...");
    }
}
