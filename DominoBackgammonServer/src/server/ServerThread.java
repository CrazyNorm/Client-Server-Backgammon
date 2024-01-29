package server;

import game.common.Game;
import server.pojo.Approve;
import server.pojo.Deny;
import server.pojo.Message;
import server.pojo.Response;
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

    private Game game;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.otherPlayers = new ArrayList<>();
    }

    public void addPlayer(Socket socket) {
        otherPlayers.add(socket);
    }

    public void setGame(Game game) {
        this.game = game;
    }


    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

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
                    if (!m.isTurn()) r.setDeny(new Deny("Wrong message"));
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
        }
    }
}
