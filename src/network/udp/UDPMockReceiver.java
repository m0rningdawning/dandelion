package network.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPMockReceiver {
    private static final int port = 9999;

    public static void main(String[] args) {
        DatagramSocket socket = null;
//        ArrayList<String> address = new ArrayList<>();

        try {
            socket = new DatagramSocket(port);
            System.out.println("UDP Receiver is running on port " + port);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received: " + receivedMessage);

//                if (address.isEmpty()){
//                    byte[] sendData = "OK".getBytes();
//                    address.addAll(Arrays.asList(receivedMessage.split(":")));
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(address.get(0)), Integer.parseInt(address.get(1)));
//                    socket.send(sendPacket);
//                }
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

