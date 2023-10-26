package echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private final static int PORT = 8081;

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(PORT);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inLine;
            while ((inLine = in.readLine()) != null) {
                out.println(inLine);
            }

        } catch (IOException e) {
            System.err.println("Exception caught listening on port " + PORT + " or listening for connection");
            System.err.println(e.getMessage());
        }
    }
}
