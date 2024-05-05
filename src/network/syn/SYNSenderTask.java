package network.syn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.udp.UDPSenderTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SYNSenderTask implements Runnable {
    private final int threadNum;
    private final int port;
    private final int targetPort;
    private final int amountOfAddresses;
    //    private final int packetsCount;
    private final int timeout;
    private final CountDownLatch latch;
    private final String[] addresses;
    private int count;

    private static final Logger logger = Logger.getLogger(UDPSenderTask.class.getName());

    SYNSenderTask(int threadNum, int port, int targetPort, int amountOfAddresses, int timeout, CountDownLatch latch) {
        this.threadNum = threadNum;
        this.port = port;
        this.targetPort = targetPort;
        this.amountOfAddresses = amountOfAddresses;
//        this.packetsCount = packetsCount;
        this.timeout = timeout;
        this.latch = latch;


        addresses = getSpoofedList(amountOfAddresses);
    }

    @Override
    public void run() {
        long startingTime = System.nanoTime();
        long lastPacketTime = startingTime;

        while (!Thread.currentThread().isInterrupted()) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastPacketTime;
            if (elapsedTime >= 0) {
                send();
                lastPacketTime = currentTime;
            }

            if (System.nanoTime() - startingTime > TimeUnit.SECONDS.toNanos(timeout)) {
                System.out.println("Thread " + Thread.currentThread().getName() + " timed out.");
                latch.countDown();
                break;
            }
        }
    }

    private String[] getSpoofedList(int amount) {
        int min = 0, max = 255;
        String[] addresses = new String[amount];

        for (int i = 0; i < amount; i++) {
            addresses[i] = "";
            for (int j = 0; j < 4; j++) {
                addresses[i] += getRandom(min, max) + (j == 3 ? "" : ".");
            }
            System.out.println(addresses[i]);
        }

        return addresses;
    }

    private int getRandom(int min, int max){
        return new Random().nextInt(max - min + 1) + min;
    }

    private void send() {
        try {
            // Yep, still local...
            InetAddress destIP = InetAddress.getLocalHost();
            InetAddress spoofedIp = InetAddress.getByName(addresses[new Random().nextInt(amountOfAddresses)]);
            int minSpoofedPort = 49152, maxSpoofedPort = 65535;
            int spoofedPort = getRandom(minSpoofedPort, maxSpoofedPort);

            ByteBuf buf = Unpooled.copiedBuffer("Packet data".getBytes());

            DatagramPacket packet = new DatagramPacket(buf.array(), new InetSocketAddress(destIP, targetPort).getPort(),
                    new InetSocketAddress(spoofedIp, spoofedPort));

            System.out.println("Packet info:" + packet);

        } catch (IOException e) {
            System.out.println("Within \"send\" method, SYNSenderTask.java: " + e.getMessage());
            logger.log(Level.SEVERE, "Within \"send\" method, SYNSenderTask.java: ", e);
            System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
        }
    }
}
