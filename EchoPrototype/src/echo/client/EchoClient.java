package echo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

    private static final int PORT = 8081;
    private static final String IP_ADDRESS = "34.127.77.157";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(IP_ADDRESS, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String input;
            while ((input = stdIn.readLine()) != null) {
                out.println(input);
                System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get IO for connection");
            System.exit(1);
        }
    }
}
