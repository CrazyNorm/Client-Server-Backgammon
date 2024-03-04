package controller;

import client.AIClientThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AIController {

    private final static int PORT = 8082;
    private static boolean listening = true;

    private final static List<AIClientThread> aiList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("controller started");
        try(ServerSocket controller = new ServerSocket(PORT);
            Socket server = controller.accept();
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()))
        ) {
            System.out.println("server connected");
            // when controller receives a message, initialise a new AI client with the given details
            String inLine;
            while (listening) {
                inLine = in.readLine();
                if (inLine == null) break;


                // parse message
                HashMap<String, String> message = new HashMap<>();
                for (String attr: inLine.split(";")) {
                    String[] split = attr.split(":", 2);
                    message.put(split[0], split[1]);
                }

                // init ai thread
                AIClientThread tempThread = new AIClientThread(
                        message.get("name"),
                        message.get("type"),
                        message.get("difficulty")
                );
                System.out.println("new ai client");
                aiList.add(tempThread);

                tempThread.start();
            }
        } catch (IOException e) {
            System.err.println("Exception listening on port " + PORT);
            System.err.println(e.getMessage());
        }
    }
}
