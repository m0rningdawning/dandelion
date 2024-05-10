package network.syn;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import network.udp.UDPSenderTask;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
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

    private int getRandom(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static byte[] buildSYNPacket(InetAddress sourceIP, InetAddress destIP, int sourcePort, int destPort) throws UnknownHostException {
        // TCP header size is 20 bytes
        byte[] tcpHeader = new byte[20];

        // Source Port (2 bytes)
        ByteBuffer.wrap(tcpHeader, 0, 2).putShort((short) sourcePort);

        // Destination Port (2 bytes)
        ByteBuffer.wrap(tcpHeader, 2, 2).putShort((short) destPort);

        // Sequence Number (4 bytes)
        ByteBuffer.wrap(tcpHeader, 4, 4).putInt(new Random().nextInt(Integer.MAX_VALUE));

        // Acknowledgment Number (4 bytes)
        ByteBuffer.wrap(tcpHeader, 8, 4).putInt(0);

        // Data Offset and Flags (2 bytes)
        short dataOffsetAndFlags = (short) ((5 << 12) | 0x02); // Data Offset = 5 (20 bytes), SYN flag set
        ByteBuffer.wrap(tcpHeader, 12, 2).putShort(dataOffsetAndFlags);

        // Window Size (2 bytes)
        ByteBuffer.wrap(tcpHeader, 14, 2).putShort((short) 8192);

        // Checksum (2 bytes) - For now, we set it to 0. It will be calculated later.
        ByteBuffer.wrap(tcpHeader, 16, 2).putShort((short) 0);

        // Urgent Pointer (2 bytes)
        ByteBuffer.wrap(tcpHeader, 18, 2).putShort((short) 0);

        // Pseudo Header (used to calculate checksum)
        byte[] pseudoHeader = new byte[12 + tcpHeader.length];
        System.arraycopy(sourceIP.getAddress(), 0, pseudoHeader, 0, 4);
        System.arraycopy(destIP.getAddress(), 0, pseudoHeader, 4, 4);
        pseudoHeader[8] = 0; // Reserved (set to 0)
        pseudoHeader[9] = 6; // Protocol (TCP)
        ByteBuffer.wrap(pseudoHeader, 10, 2).putShort((short) tcpHeader.length); // TCP Length

        // Calculate Checksum
        short checksum = calculateChecksum(pseudoHeader, tcpHeader);

        // Update Checksum in TCP Header
        ByteBuffer.wrap(tcpHeader, 16, 2).putShort(checksum);

        // Concatenate TCP Header with data (if any)
        byte[] packet = new byte[tcpHeader.length];
        System.arraycopy(tcpHeader, 0, packet, 0, tcpHeader.length);

        return packet;
    }

    private static short calculateChecksum(byte[] pseudoHeader, byte[] tcpHeader) {
        byte[] sumBytes = new byte[pseudoHeader.length + tcpHeader.length];
        System.arraycopy(pseudoHeader, 0, sumBytes, 0, pseudoHeader.length);
        System.arraycopy(tcpHeader, 0, sumBytes, pseudoHeader.length, tcpHeader.length);

        int sum = 0;
        int length = sumBytes.length;

        // Sum up 16-bit words
        for (int i = 0; i < length - 1; i += 2) {
            int word = ((sumBytes[i] << 8) & 0xFF00) + (sumBytes[i + 1] & 0xFF);
            sum += word;
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }

        // Add any remaining byte
        if (length % 2 != 0) {
            sum += (sumBytes[length - 1] << 8 & 0xFF00);
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }

        // Take the one's complement of sum
        sum = ~sum;
        sum = sum & 0xFFFF;
        return (short) sum;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    private void send() {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Yep, still local...
            InetAddress sourceIP = InetAddress.getByName(addresses[new Random().nextInt(amountOfAddresses)]);
            InetAddress destIP = InetAddress.getLocalHost();

            int minSpoofedPort = 49152, maxSpoofedPort = 65535;
            int sourcePort = getRandom(minSpoofedPort, maxSpoofedPort);

            byte[] synPacket = buildSYNPacket(sourceIP, destIP, sourcePort, targetPort);
            System.out.println("TCP SYN Packet: " + bytesToHex(synPacket));

            // Sending starts here

            DatagramPacket datagramPacket = new DatagramPacket(synPacket, synPacket.length, destIP, targetPort); // Destination port will be ignored
            socket.send(datagramPacket);

            System.out.println("Packet sent.");
        } catch (IOException e) {
            System.out.println("Within \"send\" method, SYNSenderTask.java: " + e.getMessage());
            logger.log(Level.SEVERE, "Within \"send\" method, SYNSenderTask.java: ", e);
            System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
        }
    }
}
