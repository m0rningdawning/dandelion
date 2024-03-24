package core;

/*
TODO
- Implement UDP/IP protocol
- Implement TCP/IP protocol
- Implement FTP protocol
 */

import network.TCP;
import network.UDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private UDP udp;
    private TCP tcp;
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
                "2 - TCP/IP(WIP)\n" +
                "3 - FTP(WIP)");
        System.out.println("Choose the protocol: ");
    }

    private void initiate() {
        Thread inputThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String input = reader.readLine();
                if (input.equals("1") || input.isEmpty()) {
                    udp = new UDP();
                    // Here would go other protocols
                } else if (input.equals("2") || input.equals("3")) {
                    System.out.println("Those are unimplemented, choose other.");
                } else {
                    if (udp != null){
                        udp.stop();
                        udp = null;
                    }
//                    else if (tcp != null){
//                        tcp.stop();
//                        tcp = null;
//                    }
//                    else if (ftp != null){
//                        ftp.stop();
//                        ftp = null;
//                    }
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
