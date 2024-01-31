package server;

import game.common.Game;
import game.common.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class DBGServer {

    private final static int PORT = 8081;
    private static boolean listening = true;

    private final static Random RAND = new Random();

    private final static List<ServerThread> threadList = new ArrayList<>();

    private final static Queue<ServerThread> queue = new ArrayDeque<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (listening) {
                ServerThread tempThread = new ServerThread(serverSocket.accept());
                threadList.add(tempThread);
                tempThread.start();
            }
        } catch (IOException e) {
            System.err.println("Exception listening on port " + PORT);
            System.err.println(e.getMessage());
        }
    }

    public static void joinQueue(ServerThread thread) {
        // join queue for game matching
        // setup ai games

        // if 2 players in queue, start game between them
        if (!queue.isEmpty()) {
            ServerThread opponent = queue.poll();
            thread.addPlayer(opponent.getMessageQueue(), opponent.getPlayerName());
            opponent.addPlayer(thread.getMessageQueue(), thread.getPlayerName());

            // decide colours at random
            Player colour = Player.None;
            while (colour == Player.None)
                colour = Player.values()[RAND.nextInt(Player.values().length)];
            Player opColour;
            if (colour == Player.White) opColour = Player.Black;
            else opColour = Player.White;

            // initialise game for both players
            Game game = new Game();
            thread.startGame(colour, game);
            opponent.startGame(opColour, game);
        }
        else
            queue.add(thread);
    }
}
