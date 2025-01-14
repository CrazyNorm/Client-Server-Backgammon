package com.example.dominobackgammonclient.client;

import com.example.dominobackgammonclient.client.pojo.*;
import com.example.dominobackgammonclient.client.xml.ProtocolMapper;
import com.example.dominobackgammonclient.ui.BGViewModel;
import com.example.dominobackgammonclient.ui.common.BGColour;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class ClientThread extends Thread {

    private final String address;
    private static int PORT = 8081;

    private final BGViewModel viewModel;

    private Socket socket;
    private final Queue<Object> messageQueue;

    // idempotence handling
    private final Map<String, String> responseLog;
    private final Set<String> usedTokens;


    private final ProtocolMapper protocolMapper = new ProtocolMapper();


    private final int MAX_RETRIES = 5; // how many times to retry a message before declaring a disconnect
    private int retries;

    private final int TIMEOUT = 2000; // how long to wait (ms) before retrying a message if no response received
    private long lastMessage;


    private final int KA_TIMEOUT = 10000; // how long to wait (ms) before sending a keep-alive
    private long lastActivity;


    private String name = "test";
    private String opponent;

    private PlayerPojo colour;
    private boolean connected = false;
    private boolean disconnected = false;
    private boolean gameOver = false;

    public ClientThread(String address, BGViewModel viewModel) {
        this.address = address;
        this.viewModel = viewModel;
        this.messageQueue = new ArrayDeque<>();
        this.responseLog = new HashMap<>();
        this.usedTokens = new HashSet<>();
    }

    public boolean isConnected() {
        return (connected && !disconnected);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getPlayerName() {
        return name;
    }


    public Queue<Object> getMessageQueue() {
        return messageQueue;
    }

    public void queueMessage(Message m) {
        m.setIdempotencyKey(newIdemToken());
        messageQueue.add(m);
    }


    @Override
    public void run() {

        // initialise the socket
        try {
            socket = new Socket(address, PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
            viewModel.connectionFailed();
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get IO for connection");
            viewModel.connectionFailed();
            return;
        }

        try {
            socket.setSoTimeout(TIMEOUT / 2);
        } catch (SocketException e) {
            System.err.println("Error setting socket timeout");
            viewModel.connectionFailed();
            return;
        }

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // loops receiving messages and sending responses
            lastActivity = System.currentTimeMillis();

            String lines;
            while(true) {
                // send any messages from the message queue
                int queueSize = messageQueue.size();
                for (int i = 0; i < queueSize; i++) {
                    Object o = messageQueue.poll();
                    String xml = protocolMapper.serialize(o);
                    if (o instanceof Message m) {
                        if (m.isConnect())
                            sendConnect(out, in, xml);
                        if (m.isTurn())
                            sendTurn(out, in, xml);
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
                if (lines == null || disconnected) {
                    handleDisconnect();
                    break;
                }

                if (lines.isEmpty()) {
                    if (connected) handleKeepAlive(out, in);
                    continue;
                }

                // respond to keep-alive messages
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
                        if (xml != null) {
                            out.println(xml.split("\n").length + "r");
                            out.println(xml);
                        }
                        continue;
                    }

                    // find appropriate handler
                    if (m.isStart()) handleStart(m);
                    else if (m.isTurn()) handleTurn(m);
                    else if (m.isReset()) handleReset(m);
                    else if (m.isNextTurn()) handleNextTurn(m);
                }

                // just ignore unexpected responses, wouldn't know what to do with them anyway
            }

        } catch (IOException e) {
            System.err.println("Exception listening for connection");
            System.err.println(e.getMessage());
            handleDisconnect();
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleStart(Message m) {
        // sends acknowledge response then starts game

        Response r = new Response(m.getIdempotencyKey());
        r.setAcknowledge(new Acknowledge());
        messageQueue.add(r);

        Start start = m.getStart();
        viewModel.startGame(start.getColour(), start.getOpponentName());
    }

    private void handleTurn(Message m) {
        // applies turn, then sends hash response
        // hash expects another response, but client can just ignore this

        viewModel.applyTurn(m.getTurn());

        Response r = new Response(m.getIdempotencyKey());
        r.setHash(new Hash(viewModel.checksum()));
        messageQueue.add(r);
    }

    private void handleReset(Message m) {
        // resets game state, then sends another hash response
        // hash expects another response, but client can just ignore this

        viewModel.resetGame(m.getReset());

        Response r = new Response(m.getIdempotencyKey());
        r.setHash(new Hash(viewModel.checksum()));
        messageQueue.add(r);
    }

    private void handleNextTurn(Message m) {
        // send acknowledge response, then process next turn
        // needs to handle wins, swaps & disconnects

        Response r = new Response(m.getIdempotencyKey());
        r.setAcknowledge(new Acknowledge());
        messageQueue.add(r);

        NextTurn next = m.getNextTurn();
        if (next.isWin()) {
            handleGameOver(m);
            return;
        }
        if (next.isSwap())
            viewModel.swapHands();
        viewModel.nextTurn();
    }

    private void handleGameOver(Message m) {
        if (m.getNextTurn().isDisconnect()) {
            // convert to BGColour (the player that didn't win must have disconnected)
            BGColour colour;
            if (m.getNextTurn().getWin().getPlayer() == PlayerPojo.White) colour = BGColour.BLACK;
            else colour = BGColour.WHITE;

            viewModel.disconnect(colour);
        }
        else if (m.getNextTurn().isWin()) {
            // convert to BGColour
            BGColour colour;
            if (m.getNextTurn().getWin().getPlayer() == PlayerPojo.White) colour = BGColour.WHITE;
            else colour = BGColour.BLACK;

            viewModel.win(colour, m.getNextTurn().getWin().getType());
        }

        gameOver = true;
        viewModel.gameOver();
    }

    private void handleDisconnect() {
        // self-disconnect due to lack of server response
        viewModel.disconnect();
        viewModel.gameOver();
    }


    private void sendConnect(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // sends a connect message, then waits for an approval
        out.println(xml.split("\n").length + "m");
        out.println(xml);

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, xml);
                continue;
            } else {
                retries = 0;
                lastMessage = System.currentTimeMillis();
            }

            // respond to keep-alive messages
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

            // server shouldn't be sending any messages yet, so just ignore messages

            // response type
            if (type.equals("r")) {
                Response r = protocolMapper.deserializeResponse(doc.toString());

                if (r.isApprove()) connected = true;
                break;
            }
        }

        viewModel.setConnected(connected);
    }

    private void sendTurn(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // sends a turn message, then waits for a response
        // should be an approval, view model handles a deny
        out.println(xml.split("\n").length + "m");
        out.println(xml);

        String lines;
        while(true) {
            try {
                lines = in.readLine();
            } catch (InterruptedIOException e) {
                lines = "";
            }
            if (lines == null) break;

            if (lines.isEmpty()) {
                handleRetries(out, in, xml);
                continue;
            } else {
                retries = 0;
                lastMessage = System.currentTimeMillis();
            }

            // respond to keep-alive messages
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

            // server shouldn't be sending any messages now (if client is behaving), so just ignore messages

            // response type
            if (type.equals("r")) {
                Response r = protocolMapper.deserializeResponse(doc.toString());

                if (!r.isApprove())
                    viewModel.turnDenied();
                break;
            }
        }
    }


    private void handleRetries(PrintWriter out, BufferedReader in, String xml) {
        // resends a message if timeout has been reached, and then check for disconnects

        // check for timeouts
        if (System.currentTimeMillis() >= lastMessage + TIMEOUT) {
            lastMessage = System.currentTimeMillis();
            retries++;
            out.println(xml.split("\n").length + "m");
            out.println(xml);
        }
        if (retries >= MAX_RETRIES) {
            disconnected = true;
        }
    }

    private void handleKeepAlive(PrintWriter out, BufferedReader in) throws IOException {
        // sends a short message if to check the client is still connected to the server
        // during the player's turn, it is expected there will be no messages for a time
        // but still need to check for disconnects

        if (System.currentTimeMillis() >= lastActivity + KA_TIMEOUT) {
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
