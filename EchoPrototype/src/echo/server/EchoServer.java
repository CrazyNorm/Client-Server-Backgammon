package echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private final static int PORT = 8081;

    private static boolean listening = true;

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(PORT)
        ) {
            while (listening) {
                new EchoThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Exception caught listening on port " + PORT + " or listening for connection");
            System.err.println(e.getMessage());
        }
    }


    private static class EchoThread extends Thread {

        private final Socket socket;

        public EchoThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
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
}
