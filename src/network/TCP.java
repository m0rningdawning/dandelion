package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCP {
    /*public static void main(String[] args) {
        System.out.println("Still uncooked");
    }*/
    public static Socket ClientSocket;

    public TCP(final int ServerPort) {
        try
        {
            InetAddress TargetAddress = InetAddress.getLocalHost();
            ClientSocket = new Socket(TargetAddress,ServerPort);
            System.out.println("TCP port " + ServerPort);

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
                for (int i = 0; i < 10; i++) {
                    PrintWriter outToServer = new PrintWriter(ClientSocket.getOutputStream(), true);
                    String data = "LOL there is more coming , get rekt";
                    outToServer.println(data);

                    System.out.println("Packet " + i);
                    Thread.sleep(500);
                }
                System.out.println("Finished! Press 'q' to exit.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Within \"start\" method, TCP.java: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
            }
        });
        senderThread.start();
    }

    public void stop() {
        if (ClientSocket != null && !ClientSocket.isClosed())
        {
            try
            {
                ClientSocket.close();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Stopped! Exiting...");
    }
}
