package client;

import client.xml.ProtocolMapper;
import controller.NameGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AIClientThread extends Thread {
    private final static int PORT = 8081;

    private final ProtocolMapper protocolMapper;

    private final String name;
    private final String opponent;
    private final String type;
    private final String difficulty;

    public AIClientThread(String opponent, String type, String difficulty) {
        this.protocolMapper = new ProtocolMapper();

        this.name = NameGenerator.newName();
        this.opponent = opponent;
        this.type = type;
        this.difficulty = difficulty;
    }

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("127.0.0.1", PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inLine;
            while ((inLine = in.readLine()) != null) {
                System.out.println(inLine);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get IO for connection");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        System.out.println(opponent + " " + type + " " + difficulty);
    }
}
