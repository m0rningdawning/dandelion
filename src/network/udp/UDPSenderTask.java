package network.udp;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPSenderTask implements Runnable {
    private final DatagramSocket socket;
    private final int port;
    private static final int TARGET_PORT = 8888;
    private final int packetsCount;
    private final int delay;
    private final int timeout;
    private int count = 0;

    private static final Logger logger = Logger.getLogger(UDPSenderTask.class.getName());

    UDPSenderTask(DatagramSocket socket, int port, int packetsCount, int delay, int timeout) {
        this.port = port;
        this.socket = socket;
        this.packetsCount = packetsCount;
        this.delay = delay;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        long startingTime = System.nanoTime();
        long lastPacketTime = startingTime;
//        boolean isConnected = false;

//        while (!isConnected) {
//            try {
//                if (checkForConnection(InetAddress.getLocalHost(), port)){
//                    isConnected = true;
//                }
//
//            } catch (UnknownHostException e) {
//                throw new RuntimeException(e);
//            }
//        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - lastPacketTime;
//                if (elapsedTime >= TimeUnit.SECONDS.toNanos(1) / Integer.MAX_VALUE) {
                if (elapsedTime >= 0) {
                    send();
                    lastPacketTime = currentTime;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (System.nanoTime() - startingTime > TimeUnit.SECONDS.toNanos(timeout)) {
                System.out.println("Thread " + Thread.currentThread().getName() + " timed out.");
                break;
            }
        }
    }

    private static boolean checkForConnection(InetAddress ipAddress, int port) {
        boolean isReached = false;
        boolean isReceived = false;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            byte[] sendData = ("127.0.0.1" + ":" + port).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, TARGET_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = null;

            while (!isReceived) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                if (Objects.equals(Arrays.toString((receivePacket.getData())), "OK")) {
                    isReceived = true;
                }
            }

            System.out.println("Received response from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
            isReached = true;
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout occurred. No response received.");
            throw new RuntimeException();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error within the UDPSender main \"checkForConnection()\" method ", e);
        }
        return isReached;
    }

    private void send() throws InterruptedException {
        try {
            // Everything local for now
            InetAddress targetAddress = InetAddress.getLocalHost();
//            for (int j = 0; j < packetsCount; j++) {
            String message = "Packet " + count++;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, TARGET_PORT);
            socket.send(sendPacket);
//            System.out.println("Sent: " + message);
//            Thread.sleep(delay); // Just to be safe XD
//            }
//            System.out.println("Finished! Press 'q' to exit.");
        } catch (IOException e) {
            System.out.println("Within \"start\" method, UDP.java: " + e.getMessage());
            logger.log(Level.SEVERE, "Within \"start\" method, UDP.java: ", e);
            System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
        }
    }
}
