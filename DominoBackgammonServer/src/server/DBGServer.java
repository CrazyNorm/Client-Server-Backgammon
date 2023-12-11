package server;

import java.io.IOException;
import java.net.ServerSocket;

public class DBGServer {

    private final static int PORT = 8081;
    private static boolean listening = true;


    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (listening)
                new ServerThread(serverSocket.accept()).start();
        } catch (IOException e) {
            System.err.println("Exception listening on port " + PORT);
            System.err.println(e.getMessage());
        }
    }
}
