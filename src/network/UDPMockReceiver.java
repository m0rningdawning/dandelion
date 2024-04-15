package network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPMockReceiver {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("UDP Receiver is running on port " + PORT);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received: " + receivedMessage);
            }
        } catch (IOException e) {
            System.out.println("Within \"main\" method, UDPMockReceiver.java: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

