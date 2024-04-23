package core;

/*
TODO
- Implement UDP/IP protocol
- Implement TCP/IP protocol
- Try to implement SYN Attack
- Implement FTP protocol
 */

import network.tcp.TCPSender;
import network.udp.UDPSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private UDPSender udpSender;
    private TCPSender tcpSender;
//    private FTP ftp;

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private Main() {
        printLogo();
        printInfo();
        initiate();
    }

    private void printLogo() {
        System.out.println(" ______         ____     ,---.   .--.  ______          .-''-.     .---.      .-./`)      ,-----.     ,---.   .--.\n"
                + "|    _ `''.   .'  __ `.  |    \\  |  | |    _ `''.    .'_ _   \\    | ,_|      \\ .-.')   .'  .-,  '.   |    \\  |  |\n"
                + "| _ | ) _  \\ /   '  \\  \\ |  ,  \\ |  | | _ | ) _  \\  / ( ` )   ' ,-./  )      / `-' \\  / ,-.|  \\ _ \\  |  ,  \\ |  |\n"
                + "|( ''_'  ) | |___|  /  | |  |\\_ \\|  | |( ''_'  ) | . (_ o _)  | \\  '_ '`)     `-'`\"` ;  \\  '_ /  | : |  |\\_ \\|  |\n"
                + "| . (_) `. |    _.-`   | |  _( )_\\  | | . (_) `. | |  (_,_)___|  > (_)  )     .---.  |  _`,/ \\ _/  | |  _( )_\\  |\n"
                + "|(_    ._) ' .'   _    | | (_ o _)  | |(_    ._) ' '  \\   .---. (  .  .-'     |   |  : (  '\\_/ \\   ; | (_ o _)  |\n"
                + "|  (_.\\.' /  |  _( )_  | |  (_,_)\\  | |  (_.\\.' /   \\  `-'    /  `-'`-'|___   |   |   \\ `\"/  \\  ) /  |  (_,_)\\  |\n"
                + "|       .'   \\ (_ o _) / |  |    |  | |       .'     \\       /    |        \\  |   |    '. \\_/``\".'   |  |    |  |\n"
                + "'-----'`      '.(_,_).'  '--'    '--' '-----'`        `'-..-'     `--------`  '---'      '-----'     '--'    '--'\n");
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
                        switch (input) {
                            case "1":
                                udpSender = new UDPSender();
                                break;
                            case "2":
                                tcpSender = new TCPSender();
                                break;
                            case "3":
                                System.out.println("This one is unimplemented, choose other.");
                                break;
                            default:
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
                                break;
                        }
                        break;
                    } else if (input.equals("q") || input.equals("Q")) {
                        System.out.println("Exiting...");
                        break;
                    } else {
                        System.out.println("Please choose one of the given protocols!");
                    }
                }
            } catch (IOException e) {
                //System.out.println("Within \"initiate\" method, Main.java: " + e.getMessage());
                logger.log(Level.SEVERE, "Error within the \"initiate\" method, Main.java", e);
            }
        });

        inputThread.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}
