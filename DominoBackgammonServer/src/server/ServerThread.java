package server;

import game.common.Game;
import game.common.Player;
import server.pojo.*;
import server.xml.ProtocolMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private final Socket socket;

    private final List<Socket> otherPlayers;

    private final ProtocolMapper protocolMapper = new ProtocolMapper();

    private String name;
    private String opponent;

    private Player colour;
    private Game game;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.otherPlayers = new ArrayList<>();
    }

    public void addPlayer(Socket socket, String name) {
        // used to add an opponent
        otherPlayers.add(socket);
        opponent = name;
    }

    public void addPlayer(Socket socket) {
        // used to add spectators
        otherPlayers.add(socket);
    }

    public String getPlayerName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }


    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // wait to recieve connect message before continuing
            connect(out, in);

            // wait for server to start the game
            if (game == null)
                synchronized (socket) {
                    socket.wait();
                }
            // send start message
            Message start = new Message();
            start.setStart(new Start(opponent, colour));

            String start_xml = protocolMapper.serialize(start);
            out.println(start_xml.split("\n").length + "m");
            out.println(start_xml);
            // todo: wait for acknowledge response

            // main server loop
            String lines;
            while((lines = in.readLine()) != null) {
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
                    Message m = protocolMapper.deserializeMessage(doc.toString());

                    // find appropriate response
                    Response r = new Response(m.getIdempotencyKey());
                    if (game == null) r.setDeny(new Deny("Game hasn't started"));
                    else if (!m.isTurn()) r.setDeny(new Deny("Wrong message"));
                    else if (m.getTurn().getPlayer() != colour) r.setDeny(new Deny("Wrong player"));
                    else if (!game.checkTurn(m.getTurn())) r.setDeny(new Deny("Invalid turn"));
                    else r.setApprove(new Approve());

                    // send response
                    String xml = protocolMapper.serialize(r);
                    out.println(xml.split("\n").length + "r");
                    out.println(xml);

                    // if approved, forward turn to all players
                    if (r.isApprove()) {
                        String prefix = doc.toString().split("\n").length + "m";
                        out.println(prefix);
                        out.println(doc);

                        for (Socket other: otherPlayers) {
                            PrintWriter otherOut = new PrintWriter(other.getOutputStream(), true);
                            otherOut.println(prefix);
                            otherOut.println(doc);
                        }
                    }
                }

                // response type
                else if (type.equals("r")) {
                    Response r = protocolMapper.deserializeResponse(doc.toString());

                    System.out.println(r);
                }
            }

        } catch (IOException e) {
            System.err.println("Exception listening for connection");
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(PrintWriter out, BufferedReader in) throws IOException {
        // waits to recieve a 'connect' message from the new client
        String lines;
        while((lines = in.readLine()) != null) {
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
                if (!message.isConnect()) r.setDeny(new Deny("Connect first"));
                else r.setApprove(new Approve());

                // send response
                String xml = protocolMapper.serialize(r);
                out.println(xml.split("\n").length + "r");
                out.println(xml);

                // once connect is received, register with server then stop waiting
                if (r.isApprove()) {
                    name = message.getConnect().getPlayerName();
                    DBGServer.joinQueue(this);
                    break;
                }
            }

            // response type
            else if (type.equals("r")) {
                Response response = protocolMapper.deserializeResponse(doc.toString());

                // response is reject as only accepting 'connect' messages
                Response r = new Response(response.getResponseTo());
                r.setDeny(new Deny("Connect first"));

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
}
