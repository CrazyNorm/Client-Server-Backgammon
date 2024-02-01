package server;

import game.common.Game;
import game.common.Player;
import server.pojo.*;
import server.xml.ProtocolMapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ServerThread extends Thread {

    private final Socket socket;

    private final Queue<Object> messageQueue;

    private final List<Queue<Object>> otherPlayers;

    private final ProtocolMapper protocolMapper = new ProtocolMapper();

    private final int MAX_RETRIES = 5; // how many times to retry a message before declaring a disconnect
    private int retries;

    private final int TIMEOUT = 500; // how long to wait (ms) before retrying a message if no response received
    private long lastMessage;


    private String name;
    private String opponent;

    private Player colour;
    private Game game;
    private boolean connected;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.messageQueue = new ArrayDeque<>();
        this.otherPlayers = new ArrayList<>();
    }

    public void addPlayer(Queue<Object> messageQueue, String name) {
        // used to add an opponent
        otherPlayers.add(messageQueue);
        opponent = name;
    }

    public void addPlayer(Queue<Object> messageQueue) {
        // used to add spectators
        otherPlayers.add(messageQueue);
    }

    public String getPlayerName() {
        return name;
    }

    public Queue<Object> getMessageQueue() {
        return messageQueue;
    }


    @Override
    public void run() {
        try {
            socket.setSoTimeout(TIMEOUT / 2);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // wait to recieve connect message before continuing
            handleConnect(out, in);

            // wait for server to start the game
            if (game == null)
                synchronized (socket) {
                    socket.wait();
                }
            // send start message
            sendStart(out, in);

            // main server game loop
            turnLoop(out, in);

        } catch (IOException e) {
            System.err.println("Exception listening for connection");
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnect(PrintWriter out, BufferedReader in) throws IOException {
        // waits to receive a 'connect' message from the new client
        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (lines.isEmpty()) continue;

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // message type
            if (type.equals("m")) {
                Message message = protocolMapper.deserializeMessage(doc.toString());

                // find appropriate response
                Response r = new Response(message.getIdempotencyKey());
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else if (!message.isConnect()) r.setDeny(new Deny("Connect first"));
                else r.setApprove(new Approve());

                // send response
                String xml = protocolMapper.serialize(r);
                out.println(xml.split("\n").length + "r");
                out.println(xml);

                // once connect is received, register with server then stop waiting
                if (r.isApprove()) {
                    name = message.getConnect().getPlayerName();
                    DBGServer.joinQueue(this);
                    connected = true;
                    break;
                }
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // response is reject as only accepting 'connect' messages
                Response r = new Response(response.getResponseTo());
                r.setDeny(new Deny("Incorrect response"));

                // send response
                String xml = protocolMapper.serialize(r);
                out.println(xml.split("\n").length + "r");
                out.println(xml);
            }
        }
    }


    public void startGame(Player colour, Game game) {
        this.colour = colour;
        this.game = game;
        synchronized (socket) {
            socket.notify();
        }
    }


    private void sendStart(PrintWriter out, BufferedReader in) throws IOException {
        // sends a start message then waits to receive an 'acknowledge' response
        Message start = new Message();
        start.setStart(new Start(opponent, colour));

        String start_xml = protocolMapper.serialize(start);
        out.println(start_xml.split("\n").length + "m");
        out.println(start_xml);

        // used for resending messages & disconnects
        retries = 0;
        lastMessage = System.currentTimeMillis();

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (game.getDisconnect() != Player.None) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, start_xml);
                continue;
            } else {
                retries = 0;
                lastMessage = System.currentTimeMillis();
            }

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // message type
            if (type.equals("m")) {
                Message message = protocolMapper.deserializeMessage(doc.toString());

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(message.getIdempotencyKey());
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Game hasn't started"));

                // send response
                String xml = protocolMapper.serialize(r);
                out.println(xml.split("\n").length + "r");
                out.println(xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // doesn't need a response

                // once acknowledge is received, stop waiting
                if (response.isAcknowledge())
                    break;
            }
        }
    }


    private void turnLoop(PrintWriter out, BufferedReader in) throws IOException {
        // loops receiving messages and sending responses

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            // check for disconnects
            if (game.getDisconnect() != Player.None) {
                sendDisconnect(out, in);
                break;
            }

            if (lines.isEmpty()) continue; // TODO keepalive

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // tracks if the message received is a valid turn (i.e. need next turn afterward)
            boolean validTurn = false;

            // message type
            if (type.equals("m")) {
                Message m = protocolMapper.deserializeMessage(doc.toString());

                // find appropriate response
                Response r = new Response(m.getIdempotencyKey());
                if (m.isMalformed()) r.setDeny(new Deny("Malformed"));
                else if (!m.isTurn()) r.setDeny(new Deny("Wrong message"));
                else if (m.getTurn().getPlayer() != colour) r.setDeny(new Deny("Wrong player"));
                else if (!game.checkTurn(m.getTurn())) r.setDeny(new Deny("Invalid turn"));
                else r.setApprove(new Approve());

                // queue response
                messageQueue.add(r);

                // if approved, forward turn to all players
                if (r.isApprove()) {
                    messageQueue.add(m);
                    for (Queue<Object> queue: otherPlayers)
                        queue.add(m);

                    // checkTurn has already applied turn to server game state
                    validTurn = true;
                }
            }

            // response type
            else if (type.equals("r")) {
                Response r = new Response();
                r.setDeny(new Deny("Unprompted response"));

                // queue response
                messageQueue.add(r);
            }


            // if turn was valid, send 'next turn' to all players
            if (validTurn) {
                game.nextTurn();
                // generate next turn message
                Message m = new Message();
                NextTurn next = new NextTurn(game.getCurrentPlayer());

                if (game.checkHands()) next.setSwap(new Swap());

                Player winner = game.checkWin();
                if (winner != Player.None)
                    next.setWin(new Win(winner, game.checkWinType(winner)));

                m.setNextTurn(next);

                // send next turn to all players
                messageQueue.add(m);
                for (Queue<Object> queue: otherPlayers)
                    // turn is added to message queue before next turn
                    // and thread won't continue after turn until correct checksum received
                    // therefore next turn is only sent to other players once correct checksum received
                    queue.add(m);
            }


            // send any messages from the message queue
            int queueSize = messageQueue.size();
            boolean gameOver = false;
            for (int i = 0; i < queueSize; i++) {
                Object o = messageQueue.poll();
                String xml = protocolMapper.serialize(o);
                if (o instanceof Message m) {
                    if (m.isTurn())
                        sendTurn(out, in, xml);
                    else if (m.isNextTurn()) {
                        sendNextTurn(out, in, xml);
                        if (m.getNextTurn().isWin()) gameOver = true;
                    }
                }
                else if (o instanceof Response r) {
                    out.println(xml.split("\n").length + "m");
                    out.println(xml);
                }
            }
            if (gameOver) break;
        }
    }


    private void sendTurn(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // forwards a turn message to clients & checks the correct response
        out.println(xml.split("\n").length + "m");
        out.println(xml);

        String checksum = game.checksum();

        // used for resending messages & disconnects
        retries = 0;
        lastMessage = System.currentTimeMillis();

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (game.getDisconnect() != Player.None) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, xml);
                continue;
            } else {
                retries = 0;
                lastMessage = System.currentTimeMillis();
            }

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // message type
            if (type.equals("m")) {
                Message message = protocolMapper.deserializeMessage(doc.toString());

                // response is reject as only accepting 'hash' responses
                Response r = new Response(message.getIdempotencyKey());
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // once hash is received, check it is valid then stop waiting
                boolean reset = false;
                Response r = new Response(response.getResponseTo());
                if (!response.isHash()) r.setDeny(new Deny("Incorrect response"));
                else if (!response.getHash().getValue().equals(checksum)) {
                    r.setDeny(new Deny("Inconsistent"));
                    reset = true;
                }
                else r.setApprove(new Approve());

                // send response
                String r_xml = protocolMapper.serialize(r);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);

                if (r.isApprove()) break;
                else if (reset) sendReset(out, in);
            }
        }

    }


    private void sendReset(PrintWriter out, BufferedReader in) {
        // generates and sends a reset message
        // doesn't expect a response, but will keep sending resets until it receives the correct hash for the turn

        Message reset = new Message();
        reset.setReset(new Reset(
                Arrays.asList(
                        new PieceList(Player.White, game.getPieces(Player.White)),
                        new PieceList(Player.Black, game.getPieces(Player.Black))
                ), Arrays.asList(
                        new HandPojo(Player.White,  game.getSet(Player.White), game.getDominoes(Player.White)),
                        new HandPojo(Player.White,  game.getSet(Player.White), game.getDominoes(Player.White))
        )));

        String xml = protocolMapper.serialize(reset);
        out.println(xml.split("\n").length + "m");
        out.println(xml);
    }


    private void sendNextTurn(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // send a next turn message, then wait for an 'acknowledge' response

        out.println(xml.split("\n").length + "m");
        out.println(xml);

        // used for resending messages & disconnects
        retries = 0;
        lastMessage = System.currentTimeMillis();

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (game.getDisconnect() != Player.None) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, xml);
                continue;
            } else {
                retries = 0;
                lastMessage = System.currentTimeMillis();
            }

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // message type
            if (type.equals("m")) {
                Message message = protocolMapper.deserializeMessage(doc.toString());

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(message.getIdempotencyKey());
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // doesn't need a response

                // once acknowledge is received, stop waiting
                if (response.isAcknowledge()) break;
            }
        }

    }


    private void handleRetries(PrintWriter out, BufferedReader in, String xml) {
        // resends a message if timeout has been reached, and then check for disconnects

        // check for timeouts
        if (System.currentTimeMillis() >= lastMessage + TIMEOUT) {
            System.out.println((System.currentTimeMillis() - lastMessage) + " " + retries);
            lastMessage = System.currentTimeMillis();
            retries++;
            out.println(xml.split("\n").length + "m");
            out.println(xml);
        }
        if (retries >= MAX_RETRIES) {
            System.out.println("disconnect");
            connected = false;
            if (game.getDisconnect() == Player.None)
                // only inform other players of a disconnect if no other players have disconnected
                // use shared game object to inform all players of the disconnect
                game.setDisconnect(colour);
        }
    }


    private void sendDisconnect(PrintWriter out, BufferedReader in) throws IOException {
        // generate & send 'next turn' disconnect message
        // waits for an 'acknowledge' response from all but the disconnecting player

        Message m = new Message();
        NextTurn next = new NextTurn(game.getDisconnect());
        next.setDisconnect(new Disconnect());

        Player winner;
        if (game.getDisconnect() == Player.White) winner = Player.Black;
        else winner = Player.White;
        next.setWin(new Win(winner, 1));

        m.setNextTurn(next);

        // send disconnect
        String xml = protocolMapper.serialize(m);
        out.println(xml.split("\n").length + "m");
        out.println(xml);


        // used for resending messages & disconnects
        retries = 0;
        lastMessage = System.currentTimeMillis();

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            // don't expect a response from the disconnected player
            if (game.getDisconnect() == colour || !connected) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, xml);
                continue;
            }

            // get message length & type
            String type = lines.substring(lines.length() - 1);
            lines = lines.substring(0, lines.length() - 1);

            // read message
            StringBuilder doc = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(lines); i++) {
                String inLine = in.readLine();
                inLine = inLine.replaceAll("\n", "").strip();
                doc.append(inLine);
            }

            // message type
            if (type.equals("m")) {
                Message message = protocolMapper.deserializeMessage(doc.toString());

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(message.getIdempotencyKey());
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // doesn't need a response

                // once acknowledge is received, stop waiting
                if (response.isAcknowledge()) break;
            }
        }
    }
}
