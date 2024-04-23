package core;

/*
TODO
- Implement UDP/IP protocol
- Implement TCP/IP protocol
- Implement FTP protocol
 */

import network.tcp.TCPSender;
import network.udp.UDPSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private UDPSender udpSender;
    private TCPSender tcpSender;
//    private FTP ftp;

    private Main() {
        printLogo();
        printInfo();
        initiate();
    }

    private void printLogo() {
        System.out.println("""
                 ______         ____     ,---.   .--.  ______          .-''-.     .---.      .-./`)      ,-----.     ,---.   .--.\s
                |    _ `''.   .'  __ `.  |    \\  |  | |    _ `''.    .'_ _   \\    | ,_|      \\ .-.')   .'  .-,  '.   |    \\  |  |\s
                | _ | ) _  \\ /   '  \\  \\ |  ,  \\ |  | | _ | ) _  \\  / ( ` )   ' ,-./  )      / `-' \\  / ,-.|  \\ _ \\  |  ,  \\ |  |\s
                |( ''_'  ) | |___|  /  | |  |\\_ \\|  | |( ''_'  ) | . (_ o _)  | \\  '_ '`)     `-'`"` ;  \\  '_ /  | : |  |\\_ \\|  |\s
                | . (_) `. |    _.-`   | |  _( )_\\  | | . (_) `. | |  (_,_)___|  > (_)  )     .---.  |  _`,/ \\ _/  | |  _( )_\\  |\s
                |(_    ._) ' .'   _    | | (_ o _)  | |(_    ._) ' '  \\   .---. (  .  .-'     |   |  : (  '\\_/ \\   ; | (_ o _)  |\s
                |  (_.\\.' /  |  _( )_  | |  (_,_)\\  | |  (_.\\.' /   \\  `-'    /  `-'`-'|___   |   |   \\ `"/  \\  ) /  |  (_,_)\\  |\s
                |       .'   \\ (_ o _) / |  |    |  | |       .'     \\       /    |        \\  |   |    '. \\_/``".'   |  |    |  |\s
                '-----'`      '.(_,_).'  '--'    '--' '-----'`        `'-..-'     `--------`  '---'      '-----'     '--'    '--'\s
                """);
    }

    private void printInfo() {
        System.out.println("List of protocols:\n" +
                "1 - UDP/IP\n" +
                "2 - TCP/IP\n" +
                "3 - FTP(WIP)");
        System.out.println("Choose the protocol: ");
    }

    private void initiate() {
        Thread inputThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (true) {
                    String input = reader.readLine();
                    if (input.trim().matches("[1-3]")) {
                        if (input.equals("1")) {
                            udpSender = new UDPSender();
                        }
                        else if (input.equals("2")){
                            tcpSender = new TCPSender();
                        }
                        else if (input.equals("3")) {
                            System.out.println("This one is unimplemented, choose other.");
                        } else {
                            if (udpSender != null) {
                                udpSender.stop();
                                udpSender = null;
                            } else if (tcpSender != null) {
                                tcpSender.stop();
                                tcpSender = null;
                            }
//                    else if (ftp != null){
//                        ftp.stop();
//                        ftp = null;
//                    }
                        }
                        break;
                    } else if (input.equals("q") || input.equals("Q")){
                        System.out.println("Exiting...");
                        break;
                    }
                    else {
                        System.out.println("Please choose one of the given protocols!");
                    }
                }
            } catch (IOException e) {
                System.out.println("Within \"initiate\" method, Main.java: " + e.getMessage());
            }
        });

        inputThread.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}
