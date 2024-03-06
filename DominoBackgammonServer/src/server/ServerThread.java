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

    // idempotence handling
    private final Map<String, String> responseLog;
    private final Set<String> usedTokens;

    private final List<Queue<Object>> otherPlayers;

    private final ProtocolMapper protocolMapper = new ProtocolMapper();


    private final int MAX_RETRIES = 5; // how many times to retry a message before declaring a disconnect
    private int retries;

    private final int TIMEOUT = 2000; // how long to wait (ms) before retrying a message if no response received
    private long lastMessage;


    private final int KA_TIMEOUT = 10000; // how long to wait (ms) before sending a keep-alive
    private long lastActivity;


    private String name;
    private String opponent;
    private boolean ai;

    private Player colour;
    private Game game;
    private boolean connected;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.messageQueue = new ArrayDeque<>();
        this.responseLog = new HashMap<>();
        this.usedTokens = new HashSet<>();
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
    public String getOpponentType() {
        return opponent;
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
            // wait to receive connect message before continuing
            handleConnect(out, in);

            // wait for server to start the game
            waitForStart(out, in);

            if (game != null) {
                // send start message (doesn't bother for ai)
                if (!ai) sendStart(out, in);
                else if (colour == Player.White) sendAITurn(out, in);


                // main server game loop
                turnLoop(out, in);
            }

        } catch (IOException e) {
            System.err.println("Exception listening for connection");
            System.err.println(e.getMessage());
            if (game != null) game.setDisconnect(colour);
        }
        System.out.println(name + " disconnected");
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

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

                // check idempotency
                String idem_tok = message.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml = responseLog.get(idem_tok);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);
                    continue;
                }

                // find appropriate response
                Response r = new Response(idem_tok);
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else if (!message.isConnect()) r.setDeny(new Deny("Connect first"));
                else if (message.getConnect().getOpponentType().startsWith("ai:")){
                    String opponentType = message.getConnect().getOpponentType();
                    if (!opponentType.matches("^ai:(random|(minimax|mcts)-[1-5])$"))
                        r.setDeny(new Deny("Invalid AI config"));
                    else if (!DBGServer.isAIAvailable()) r.setDeny(new Deny("AI unavailable"));
                    else r.setApprove(new Approve());
                } else r.setApprove((new Approve()));

                // send response
                String xml = protocolMapper.serialize(r);
                responseLog.put(idem_tok, xml);
                out.println(xml.split("\n").length + "r");
                out.println(xml);

                // once connect is received, register with server then stop waiting
                if (r.isApprove()) {
                    name = message.getConnect().getPlayerName();
                    opponent = message.getConnect().getOpponentType();
                    ai = message.getConnect().isAI();
                    System.out.println(name + " connected against " + opponent);
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


    private void waitForStart(PrintWriter out, BufferedReader in) throws IOException {
        // handles keep-alives while waiting for an opponent

        lastActivity = System.currentTimeMillis();

        String lines;
        while(game == null) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null || !connected) {
                DBGServer.leaveQueue(this);
                break;
            }

            if (lines.isEmpty()) {
                if (!ai) handleKeepAlive(out, in);
                continue;
            }

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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
                Message m = protocolMapper.deserializeMessage(doc.toString());

                // check idempotency
                String idem_tok = m.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml = responseLog.get(idem_tok);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);
                    continue;
                }

                // always reject as shouldn't be sending anything yet
                Response r = new Response(idem_tok);
                r.setDeny(new Deny("Game hasn't started yet"));

                // send response
                String xml = protocolMapper.serialize(r);
                out.println(xml.split("\n").length + "r");
                out.println(xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // always reject as shouldn't be sending anything yet
                Response r = new Response(response.getResponseTo());
                r.setDeny(new Deny("Game hasn't started yet"));

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
    }


    private void sendStart(PrintWriter out, BufferedReader in) throws IOException {
        // sends a start message then waits to receive an 'acknowledge' response
        Message start = new Message(newIdemToken());
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

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

                // check idempotency
                String idem_tok = message.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml = responseLog.get(idem_tok);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);
                    continue;
                }

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(idem_tok);
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Game hasn't started"));

                // send response
                String xml = protocolMapper.serialize(r);
                responseLog.put(idem_tok, xml);
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

        lastActivity = System.currentTimeMillis();

        String lines;
        while(true) {
            // send any messages from the message queue
            int queueSize = messageQueue.size();
            boolean gameOver = false;
            for (int i = 0; i < queueSize; i++) {
                Object o = messageQueue.poll();
                String xml = protocolMapper.serialize(o);
                if (o instanceof Message m) {
                    if (m.isTurn() && !ai)
                        sendTurn(out, in, xml);
                    else if (m.isNextTurn()) {
                        if (!ai) sendNextTurn(out, in, xml);
                        if (m.getNextTurn().isWin()) gameOver = true;
                        if (m.getNextTurn().isSwap() && colour == game.getCurrentPlayer())
                            game.swapHands();
                        if (ai && !gameOver) {
                            sendAITurn(out, in);
                        }
                    }
                }
                else if (o instanceof Response r) {
                    responseLog.put(r.getResponseTo(), xml);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);
                }
            }
            if (gameOver) break;

            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) {
                game.setDisconnect(colour);
                break;
            }

            // check for disconnects
            if (game.getDisconnect() != Player.None) {
                sendDisconnect(out, in);
                break;
            }

            if (lines.isEmpty()) {
                if (!ai) handleKeepAlive(out, in);
                continue;
            }

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

            // tracks if the message received is a valid turn (i.e. need next turn afterward)
            boolean validTurn = false;

            // message type
            if (type.equals("m")) {
                Message m = protocolMapper.deserializeMessage(doc.toString());

                // check idempotency
                String idem_tok = m.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml = responseLog.get(idem_tok);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);
                    continue;
                }

                // find appropriate response
                Response r = new Response(idem_tok);
                if (m.isMalformed()) r.setDeny(new Deny("Malformed"));
                else if (!m.isTurn()) r.setDeny(new Deny("Wrong message"));
                else if (m.getTurn().getPlayer() != colour) r.setDeny(new Deny("Wrong player"));
                else if (!game.checkTurn(m.getTurn())) r.setDeny(new Deny("Invalid turn"));
                else r.setApprove(new Approve());

                // queue response
                messageQueue.add(r);

                // if approved, forward turn to all players
                if (r.isApprove()) {
                    m.setIdempotencyKey(newIdemToken());
                    messageQueue.add(m);
                    for (Queue<Object> queue: otherPlayers)
                        queue.add(m);

                    // checkTurn has already applied turn to server game state
                    validTurn = true;
                }
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                Response r = new Response();
                // always validate hashes, even if unexpected
                boolean reset = false;
                if (!response.isHash()) r.setDeny(new Deny("Unprompted response"));
                else if (!response.getHash().getValue().equals(game.checksum())) {
                    r.setDeny(new Deny("Inconsistent"));
                    reset = true;
                } else r.setApprove(new Approve());

                // queue response
                messageQueue.add(r);
                if (reset) {
                    sendReset(out, in);
                }
            }


            // if turn was valid, send 'next turn' to all players
            if (validTurn) {
                game.nextTurn();
                // generate next turn message
                Message m = new Message(newIdemToken());
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

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

                // check idempotency
                String idem_tok = message.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml_i = responseLog.get(idem_tok);
                    out.println(xml_i.split("\n").length + "r");
                    out.println(xml_i);
                    continue;
                }

                // response is reject as only accepting 'hash' responses
                Response r = new Response(idem_tok);
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                responseLog.put(idem_tok, r_xml);
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

                if (r.isApprove()) {
                    //
                    responseLog.put(response.getResponseTo(), r_xml);
                    break;
                }
                else if (reset) sendReset(out, in);
            }
        }

    }


    private void sendReset(PrintWriter out, BufferedReader in) {
        // generates and sends a reset message
        // doesn't expect a response, but will keep sending resets until it receives the correct hash for the turn

//        System.out.println("reset " + name);

        Message reset = new Message(newIdemToken());
        reset.setReset(new Reset(
                game.getCurrentPlayer(),
                game.getTurnCount(),
                game.hasSwapped(),
                Arrays.asList(
                    new PieceList(Player.White, game.getPieces(Player.White)),
                    new PieceList(Player.Black, game.getPieces(Player.Black))
                ), Arrays.asList(
                    new HandPojo(Player.White, game.getSet(Player.White), game.getDominoes(Player.White)),
                    new HandPojo(Player.Black, game.getSet(Player.Black), game.getDominoes(Player.Black))
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

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

                // check idempotency
                String idem_tok = message.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml_i = responseLog.get(idem_tok);
                    out.println(xml_i.split("\n").length + "r");
                    out.println(xml_i);
                    continue;
                }

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(idem_tok);
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                responseLog.put(idem_tok, r_xml);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // always validate hashes, even if unexpected
                if (response.isHash()) {
                    Response r = new Response();
                    boolean reset = false;
                    if (!response.getHash().getValue().equals(game.checksum())) {
                        r.setDeny(new Deny("Inconsistent"));
                        reset = true;
                    } else r.setApprove(new Approve());


                    // send response
                    String r_xml = protocolMapper.serialize(r);
                    out.println(r_xml.split("\n").length + "r");
                    out.println(r_xml);
                    if (reset) sendReset(out, in);
                }

                // otherwise, doesn't need a response

                // once acknowledge is received, stop waiting
                if (response.isAcknowledge()) break;
            }
        }

    }


    private void sendAITurn(PrintWriter out, BufferedReader in) throws IOException {
        // sends a reset message to an AI client to trigger the AI taking its turn
        if (game.getCurrentPlayer() == colour)
            sendReset(out, in);
        // doesn't expect any response yet
    }


    private void handleRetries(PrintWriter out, BufferedReader in, String xml) {
        // resends a message if timeout has been reached, and then check for disconnects

        // check for timeouts
        if (System.currentTimeMillis() >= lastMessage + TIMEOUT) {
//            System.out.println((System.currentTimeMillis() - lastMessage) + " " + retries);
            lastMessage = System.currentTimeMillis();
            retries++;
            out.println(xml.split("\n").length + "m");
            out.println(xml);
        }
        if (retries >= MAX_RETRIES) {
            connected = false;
            if (game.getDisconnect() == Player.None)
                // only inform other players of a disconnect if no other players have disconnected
                // use shared game object to inform all players of the disconnect
                if (game != null) game.setDisconnect(colour);
        }
    }


    private void sendDisconnect(PrintWriter out, BufferedReader in) throws IOException {
        // generate & send 'next turn' disconnect message
        // waits for an 'acknowledge' response from all but the disconnecting player

        Message m = new Message(newIdemToken());
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

            if (lines.matches("<ka>[mrMR]</ka>")) {
                lastActivity = System.currentTimeMillis();
                // send keep-alive
                if (lines.matches("<ka>[mM]</ka>")) {
                    KeepAlive ka = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka));
                }
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

                // check idempotency
                String idem_tok = message.getIdempotencyKey();
                usedTokens.add(idem_tok);
                if (responseLog.containsKey(idem_tok)) {
                    String xml_i = responseLog.get(idem_tok);
                    out.println(xml_i.split("\n").length + "r");
                    out.println(xml_i);
                    continue;
                }

                // response is reject as only accepting 'acknowledge' responses
                Response r = new Response(idem_tok);
                if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                else r.setDeny(new Deny("Expecting response"));

                // send response
                String r_xml = protocolMapper.serialize(r);
                responseLog.put(idem_tok, r_xml);
                out.println(r_xml.split("\n").length + "r");
                out.println(r_xml);
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // always validate hashes, even if unexpected
                if (response.isHash()) {
                    Response r = new Response();
                    boolean reset = false;
                    if (!response.getHash().getValue().equals(game.checksum())) {
                        r.setDeny(new Deny("Inconsistent"));
                        reset = true;
                    } else r.setApprove(new Approve());


                    // send response
                    String r_xml = protocolMapper.serialize(r);
                    out.println(r_xml.split("\n").length + "r");
                    out.println(r_xml);
                    if (reset) sendReset(out, in);
                }

                // otherwise, doesn't need a response

                // once acknowledge is received, stop waiting
                if (response.isAcknowledge()) break;
            }
        }
    }


    private void handleKeepAlive(PrintWriter out, BufferedReader in) throws IOException {
        // sends a short message if to check the client is still connected
        // during the player's turn, it is expected there will be no messages for a time
        // but still need to check for disconnects

        if (System.currentTimeMillis() >= lastActivity + KA_TIMEOUT) {
//            System.out.println((System.currentTimeMillis() - lastActivity) + " " + this.getName());

            // send keep-alive
            KeepAlive ka = new KeepAlive("m");
            String xml = protocolMapper.serialize(ka);
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

                // check for disconnects
                if (game != null && game.getDisconnect() != Player.None) break;
                if (!connected) break;

                if (lines.isEmpty()) {
                    handleRetries(out, in, xml);
                    continue;
                }

                if (lines.matches("<ka>[mrMR]</ka>")) {
                    lastActivity = System.currentTimeMillis();
                    if (lines.matches("<ka>[rR]</ka>")) break;

                    // send keep-alive
                    KeepAlive ka_r = new KeepAlive("r");
                    out.println(protocolMapper.serialize(ka_r));
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

                    // check idempotency
                    String idem_tok = message.getIdempotencyKey();
                    usedTokens.add(idem_tok);
                    if (responseLog.containsKey(idem_tok)) {
                        String xml_i = responseLog.get(idem_tok);
                        out.println(xml_i.split("\n").length + "r");
                        out.println(xml_i);
                        continue;
                    }

                    // response is reject as only accepting 'keep-alive' responses
                    Response r = new Response(idem_tok);
                    if (message.isMalformed()) r.setDeny(new Deny("Malformed"));
                    else r.setDeny(new Deny("Expecting keep-alive"));

                    // send response
                    String r_xml = protocolMapper.serialize(r);
                    responseLog.put(idem_tok, r_xml);
                    out.println(r_xml.split("\n").length + "r");
                    out.println(r_xml);
                }

                // response type
                else if (type.equals("r")) {
                    Response response = protocolMapper.deserializeResponse(doc.toString());

                    // always validate hashes, even if unexpected
                    if (response.isHash()) {
                        Response r = new Response();
                        boolean reset = false;
                        if (!response.getHash().getValue().equals(game.checksum())) {
                            r.setDeny(new Deny("Inconsistent"));
                            reset = true;
                        } else r.setApprove(new Approve());


                        // send response
                        String r_xml = protocolMapper.serialize(r);
                        out.println(r_xml.split("\n").length + "r");
                        out.println(r_xml);
                        if (reset) sendReset(out, in);
                        continue;
                    }

                    // otherwise, response is reject as only accepting 'keep-alive' responses
                    Response r = new Response(response.getResponseTo());
                    if (response.isMalformed()) r.setDeny(new Deny("Malformed"));
                    else r.setDeny(new Deny("Expecting keep-alive"));

                    // send response
                    String r_xml = protocolMapper.serialize(r);
                    out.println(r_xml.split("\n").length + "r");
                    out.println(r_xml);
                }
            }
        }
    }


    private String newIdemToken() {
        // generates a new random idempotency key
        String idem = UUID.randomUUID().toString();

        // check if token has already been used (incredibly unlikely but safer to check)
        while (usedTokens.contains(idem)) idem = UUID.randomUUID().toString();

        usedTokens.add(idem);
        return idem;
    }
}
