package com.example.dominobackgammonclient.client;

import com.example.dominobackgammonclient.client.pojo.*;
import com.example.dominobackgammonclient.client.xml.ProtocolMapper;
import com.example.dominobackgammonclient.game.common.Game;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ClientThread extends Thread {

    private final Socket socket;
    private final Queue<Object> messageQueue;


    private final ProtocolMapper protocolMapper = new ProtocolMapper();


    private final int MAX_RETRIES = 5; // how many times to retry a message before declaring a disconnect
    private int retries;

    private final int TIMEOUT = 500; // how long to wait (ms) before retrying a message if no response received
    private long lastMessage;


    private final int KA_TIMEOUT = 5000; // how long to wait (ms) before sending a keep-alive
    private long lastActivity;


    private String name = "test";
    private String opponent;

    private PlayerPojo colour;
    private Game game;
    private boolean connected = false;

    public ClientThread(Socket socket) {
        this.socket = socket;
        this.messageQueue = new ArrayDeque<>();
    }

    public String getPlayerName() {
        return name;
    }


    public Queue<Object> getMessageQueue() {
        return messageQueue;
    }

    public void queueMessage(Message m) {
        messageQueue.add(m);
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
            // loops receiving messages and sending responses
            lastActivity = System.currentTimeMillis();

            String lines;
            while(true) {
                try {
                    lines = in.readLine();
                } catch (InterruptedIOException e) {
                    lines = "";
                }
                if (lines == null) {
                    // todo: handle disconnect from server
                    break;
                }

                if (lines.isEmpty()) {
                    // todo: send keep-alives during turns
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

                    // find appropriate handler
                    if (m.isMalformed()) handleMalformed(out, in);
                    else if (m.isStart()) handleStart(out, in, m);
                    else if (m.isTurn()) handleTurn(out, in, m);
                    else if (m.isReset()) handleReset(out, in, m);
                    else if (m.isNextTurn()) handleNextTurn(out, in, m);
                }

                // response type
                else if (type.equals("r")) {
                    // todo: unexpected response?
                }


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
                        out.println(xml.split("\n").length + "m");
                        out.println(xml);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Exception listening for connection");
            System.err.println(e.getMessage());
        }
    }


    private void handleStart(PrintWriter out, BufferedReader in, Message m) {
        // sends acknowledge response then starts game

        Response r = new Response(m.getIdempotencyKey());
        r.setAcknowledge(new Acknowledge());
        messageQueue.add(r);

        // todo: view model startGame()
    }

    private void handleTurn(PrintWriter out, BufferedReader in, Message m) {
        // applies turn, then sends hash response
        // hash expects another response, but client can just ignore this

        // todo: view model applyTurn()

        Response r = new Response(m.getIdempotencyKey());
        r.setHash(new Hash(game.checksum()));
        messageQueue.add(r);
    }

    private void handleReset(PrintWriter out, BufferedReader in, Message m) {
        // resets game state, then sends another hash response
        // hash expects another response, but client can just ignore this

        // todo: view model resetGame()

        Response r = new Response(m.getIdempotencyKey());
        r.setHash(new Hash(game.checksum()));
        messageQueue.add(r);
    }

    private void handleNextTurn(PrintWriter out, BufferedReader in, Message m) {
        handleGameOver(out, in);
    }

    private void handleGameOver(PrintWriter out, BufferedReader in) {

    }

    private void handleMalformed(PrintWriter out, BufferedReader in) {

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
                // todo: retries
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

            // server shouldn't be sending any messages yet, so just ignore messages

            // response type
            if (type.equals("r")) {
                Response r = protocolMapper.deserializeResponse(doc.toString());

                if (r.isApprove()) connected = true;
                break;
            }
        }
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
                // todo: retries
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

            // server shouldn't be sending any messages now (if client is behaving), so just ignore messages

            // response type
            if (type.equals("r")) {
                Response r = protocolMapper.deserializeResponse(doc.toString());

//                if (!r.isApprove())
                    // todo: view model turnDenied()
                break;
            }
        }
    }
}
