package network;

import java.io.*;
import java.net.*;

public class UDPMockClient {
    private static final int CLIENT_PORT = 8888;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(CLIENT_PORT);
            System.out.println("UDP Client is running on port " + CLIENT_PORT);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received from server: " + receivedMessage);
            }
        } catch (IOException e) {
            System.out.println("Within \"main\" method, UDPMockClient.java: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

