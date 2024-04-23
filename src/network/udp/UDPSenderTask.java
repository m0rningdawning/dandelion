package network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class UDPSenderTask implements Runnable {
    private final DatagramSocket socket;
    private final int packetsCount;
    private final int delay;
    private final int timeout;
    private int count = 0;

    UDPSenderTask(DatagramSocket socket, int packetsCount, int delay, int timeout) {
        this.socket = socket;
        this.packetsCount = packetsCount;
        this.delay = delay;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        long startingTime = System.nanoTime();
        long lastPacketTime = startingTime;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - lastPacketTime;
                if (elapsedTime >= TimeUnit.SECONDS.toNanos(1) / 100000000) {
                    start();
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

    private void start() throws InterruptedException {
        try {
            // Everything local for now
            InetAddress targetAddress = InetAddress.getLocalHost();
            int targetPort = 8888;

//            for (int j = 0; j < packetsCount; j++) {
            String message = "Packet " + count++;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
            socket.send(sendPacket);
//                    System.out.println("Sent: " + message);
//            Thread.sleep(delay); // Just to be safe XD
//            }
            System.out.println("Finished! Press 'q' to exit.");
        } catch (IOException e) {
            System.out.println("Within \"start\" method, UDP.java: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
        }
    }
}
