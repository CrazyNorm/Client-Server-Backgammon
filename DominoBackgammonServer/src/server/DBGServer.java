package server;

import game.common.Game;
import game.common.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DBGServer {

    private final static int PORT = 8081;
    private static boolean listening = true;

    private final static Random RAND = new Random();

    private final static List<ServerThread> threadList = new ArrayList<>();

    private final static Queue<ServerThread> queue = new ArrayDeque<>();

    private static Socket controllerSocket;
    private static PrintWriter controllerOut;
    private static final String CONTROLLER_ADDRESS = "10.0.0.134";
    private static final int CONTROLLER_PORT = 8082;

    // uses a UUID for AI clients to identify their opponent
    private static final Set<String> usedAIKeys = new HashSet<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            // output network interface info
            for (NetworkInterface netint: NetworkInterface.networkInterfaces().toList()) {
                System.out.println(netint.getDisplayName() + " (" + netint.getName() + ")");
                for (InetAddress address: Collections.list(netint.getInetAddresses()))
                    System.out.println(address);
                System.out.println();
            }

            // initialise AI controller connection
            try {
                controllerSocket = new Socket(CONTROLLER_ADDRESS, CONTROLLER_PORT);
                controllerOut = new PrintWriter(controllerSocket.getOutputStream(), true);
            } catch(IOException e) {
                System.out.println("AI unavailable");
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

        // check for a valid opponent in the queue
        ServerThread opponent = null;
        // if the new client is an AI
        if (thread.isAi()) {
            for (ServerThread player: queue) {
                if (thread.getOpponentType().equals("name:" + player.getAiKey())) {
                    opponent = player;
                    queue.remove(player);
                    break;
                }
            }
        }
        // if new client has no opponent preference
        else if (thread.getOpponentType().equals("any"))
            for (ServerThread player: queue) {
                if (player.isAi()) continue;
                if (player.getOpponentType().equals("any") ||
                        player.getOpponentType().equals("name:" + thread.getPlayerName())) {
                    opponent = player;
                    queue.remove(player);
                    break;
                }
            }
        // if new client requests a specific opponent name
        else if (thread.getOpponentType().startsWith("name:")) {
            String opponentName = thread.getOpponentType().substring(5);
            for (ServerThread player: queue) {
                if (player.isAi()) continue;
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
        // if new client requests and ai opponent
        else if (thread.getOpponentType().startsWith("ai:")) {
            // client specifies ai request as 'ai:[type]-[difficulty]'

            // generate AI opponent key
            String newKey = UUID.randomUUID().toString();
            while (usedAIKeys.contains(newKey)) newKey = UUID.randomUUID().toString();
            usedAIKeys.add(newKey);
            thread.setAiKey(newKey);

            String aiDetails = "name:" + newKey + ";";

            String aiType = thread.getOpponentType().substring(3);
            aiDetails += "type:" + aiType.split("-", 2)[0] + ";";
            if (!aiType.equals("random"))
                aiDetails += "difficulty:" + aiType.split("-", 2)[1] + ";";

            // sends ai details to controller to initialise an ai opponent
            controllerOut.println(aiDetails);

            // doesn't specify an opponent, so new client joins queue until new ai connects
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
        else if (!thread.isAi()) queue.add(thread);
    }


    public static void leaveQueue(ServerThread thread) {
        // leave game matching queue when client disconnects

        queue.remove(thread);
    }


    public static boolean isAIAvailable() {
        // checks if AI controller is available
        return (controllerOut != null);
    }
}
