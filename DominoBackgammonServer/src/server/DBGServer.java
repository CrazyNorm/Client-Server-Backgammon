package server;

import game.common.Game;
import game.common.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
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
            // output network interface info
            for (NetworkInterface netint: NetworkInterface.networkInterfaces().toList()) {
                System.out.println(netint.getDisplayName() + " (" + netint.getName() + ")");
                for (InetAddress address: Collections.list(netint.getInetAddresses()))
                    System.out.println(address);
                System.out.println();
            }
            System.out.println();

            while (listening) {
                ServerThread tempThread = new ServerThread(serverSocket.accept());
                System.out.println("new client");
                threadList.add(tempThread);
                tempThread.start();
            }
        } catch (IOException e) {
            System.err.println("Exception listening on port " + PORT);
            System.err.println(e.getMessage());
        }
    }

    public static void joinQueue(ServerThread thread) {
        // todo: check periodically
        // join queue for game matching
        // setup ai games

        // check for a valid opponent in the queue
        ServerThread opponent = null;
        // if new client has no opponent preference
        if (thread.getOpponentType().equals("any"))
            for (ServerThread player: queue)
                if (player.getOpponentType().equals("any") ||
                        player.getOpponentType().equals("name:" + thread.getPlayerName())) {
                    opponent = player;
                    queue.remove(player);
                    break;
                }
        // if new client requests a specific opponent name
        if (thread.getOpponentType().startsWith("name:")) {
            String opponentName = thread.getOpponentType().substring(5);
            for (ServerThread player: queue) {
                if (player.getPlayerName().equals(opponentName)) {
                    // check waiting player is ok to player against new client
                    if (player.getOpponentType().equals("any") ||
                            player.getOpponentType().equals("name:" + thread.getPlayerName())) {
                        opponent = player;
                        queue.remove(player);
                        break;
                    }
                }
            }
        }

        // start game if an opponent id found
        if (opponent != null) {
            // register opponents for each thread
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


    public static void leaveQueue(ServerThread thread) {
        // leave game matching queue when client disconnects

        queue.remove(thread);
    }
}
