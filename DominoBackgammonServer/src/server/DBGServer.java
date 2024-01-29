package server;

import game.common.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class DBGServer {

    private final static int PORT = 8081;
    private static boolean listening = true;

    private static List<ServerThread> threadList = new ArrayList<>();

    private static List<ServerThread> waiting = new ArrayList<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (listening) {
                ServerThread tempThread = new ServerThread(serverSocket.accept());
                threadList.add(tempThread);
                tempThread.setGame(new Game());
                tempThread.start();
            }
        } catch (IOException e) {
            System.err.println("Exception listening on port " + PORT);
            System.err.println(e.getMessage());
        }
    }
}
