package client;

import ai.AI;
import ai.AIFactory;
import client.pojo.Connect;
import client.pojo.KeepAlive;
import client.pojo.Message;
import client.pojo.Response;
import client.xml.ProtocolMapper;
import controller.NameGenerator;
import game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class AIClientThread extends Thread {
    private final static int PORT = 8081;
    private final static String SERVER_ADDRESS = "10.0.0.245";

    private final ProtocolMapper protocolMapper;

    // idempotence handling
    private final Map<String, String> messageLog;
    private final Set<String> usedTokens;


    private final String name;
    private final String opponent;
    private final String type;
    private final String difficulty;

    private final AI aiProfile;

    public AIClientThread(String opponent, String type, String difficulty) {
        this.protocolMapper = new ProtocolMapper();

        this.name = NameGenerator.newName();
        this.opponent = opponent;
        this.type = type;
        this.difficulty = difficulty;

        this.aiProfile = AIFactory.getAIProfile(type, difficulty);

        this.messageLog = new HashMap<>();
        this.usedTokens = new HashSet<>();
    }


    @Override
    public void run() {
        try (Socket clientSocket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // connect AI client to server
            Message connect = new Message(newIdemToken());
            connect.setConnect(new Connect(name, "name:" + opponent, true));
            sendConnect(out, in, protocolMapper.serialize(connect));

            String lines;
            while((lines = in.readLine()) != null) {
                // respond to keep-alive messages
                if (lines.matches("<ka>[mrMR]</ka>")) {
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

                /// message type
                if (type.equals("m")) {
                    Message m = protocolMapper.deserializeMessage(doc.toString());

                    // check idempotency
                    String idem_tok = m.getIdempotencyKey();
                    usedTokens.add(idem_tok);
                    if (messageLog.containsKey(idem_tok)) {
                        String xml = messageLog.get(idem_tok);
                        out.println(xml.split("\n").length + "m");
                        out.println(xml);
                        continue;
                    }

                    if (m.isNextTurn() && m.getNextTurn().isDisconnect()) break;

                    if (m.isReset()) {
                        Game game = Game.gameFromReset(m.getReset());

                        boolean validTurn = false;
                        while (!validTurn) {
                            Message turn = new Message(newIdemToken());
                            turn.setTurn(aiProfile.chooseTurn(game));

                            String xml = protocolMapper.serialize(turn);
                            messageLog.put(m.getIdempotencyKey(), xml);
                            validTurn = sendTurn(out, in, xml);
                        }
                    }
                }

                // not expecting any responses at the moment, so just ignore any that do arrive
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get IO for connection");
            System.exit(1);
        }
    }


    private void sendConnect(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // sends a connect message, then waits for an approval
        out.println(xml.split("\n").length + "m");
        out.println(xml);

        String lines;
        while((lines = in.readLine()) != null) {
            // respond to keep-alive messages
            if (lines.matches("<ka>[mrMR]</ka>")) {
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

                if (r.isApprove()) break;
            }
        }
    }


    private boolean sendTurn(PrintWriter out, BufferedReader in, String xml) throws IOException {
        // sends a turn, then waits for an approval

        out.println(xml.split("\n").length + "m");
        out.println(xml);

        String lines;
        boolean valid = true;
        while((lines = in.readLine()) != null) {
            // respond to keep-alive messages
            if (lines.matches("<ka>[mrMR]</ka>")) {
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

                if (r.isDeny()) valid = false;
                break;
            }
        }
        return valid;
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
