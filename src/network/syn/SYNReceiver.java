package network.syn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class SYNReceiver {
    private final int port;

    public SYNReceiver(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.bind(new InetSocketAddress(port));

            System.out.println("SYN Receiver started on port " + port);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                socket.receive(packet);

                String data = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() +
                        ":" + packet.getPort() + " - Data: " + data);
            }
        } catch (Exception e) {
            System.out.println("Error in SYNReceiver: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 9999;
        SYNReceiver receiver = new SYNReceiver(port);
        receiver.start();
    }
}
