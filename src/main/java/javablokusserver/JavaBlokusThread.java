package javablokusserver;

import java.net.*;
import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JavaBlokusThread extends Thread {
    protected Socket conn;
    static Vector<JavaBlokusThread> threads;
    static int assignId = 0; // 0 or 1

    static public Communication comObj;

    String playerName;
    int playerId;

    public ObjectMapper mapper;


    public JavaBlokusThread(Socket s) {
        conn = s;
        if (threads == null) {
            threads = new Vector<>();
        }
        threads.add(this);
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public void run() {
        try {
            System.out.println("Connected");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream())); // データ受信用バッファの設定


            // プレイヤーの名前を受け取る
            playerName = in.readLine();
            System.out.println("Get Player Name : " + playerName);

            // プレイヤーのIDを渡す
            playerId = assignId;
            sendPlayerId(playerId);
            assignId = (assignId + 1) % 2;

            if(threads.size() == JavaBlokusServer.PLAYER_NUM) {
//                System.out.println("Start Blokus!");
                sendToClients("Start Blokus!");
                comObj = new Communication();
                initObj();
//                System.out.println("initObj: \n" + comObj);
                try{
//                    System.out.println("json: \n" + mapper.writeValueAsString(comObj));
                    sendToClients(mapper.writeValueAsString(comObj));
                } catch (JsonProcessingException jpe) {
                    System.err.println(jpe);
                }
            }

            try {
                while (true) {
                    try {
                        String objJson = in.readLine();
                        if (objJson == null) {
                            conn.close();
                            threads.remove(this);
                            return;
                        }

                        comObj = mapper.readValue(objJson, Communication.class);
                        comObj.updateTurn();

                        if(comObj.giveup) {
                            comObj.whowon = comObj.players[comObj.turn];
                            comObj.finished = true;
                            sendToClients(mapper.writeValueAsString(comObj));
                            closeConnection();
                            break;
                        }

                        sendToClients(mapper.writeValueAsString(comObj));
                    } catch (IOException e) {
                        System.err.println("Connection Closed! (Exception)");
                        comObj.finished = true;
                        sendToClients(mapper.writeValueAsString(comObj));
                        closeConnection();
                        threads.removeAllElements();
                        return;
                    }


                }
            } catch (IOException e) {
                System.err.println(e);
            }

            conn.close();
            System.out.println("Connection Closed");
            threads.remove(this);
        } catch (IOException e) {
            System.err.println(e);
            threads.remove(this);
        }
    }

    private void sendToClients(String msg) {
        for (JavaBlokusThread th : threads) {
            if (th.isAlive()) {
                th.sendToClient(th, msg);
            }
        }
    }

    public void sendToClient(JavaBlokusThread th, String msg) {
        try{
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    conn.getOutputStream())),
                    true); // 送信バッファ設定
            out.println(msg);
//            System.out.println("Send " + msg + "\nto " + th);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void closeConnection() {
        for(int i = 0; i < threads.size(); i++) {
            if(this == threads.get(i)) continue;
            JavaBlokusThread th = threads.get(i);
            if(th.isAlive()) {
                th.closeConn(th);
            }
        }
    }

    public void closeConn(JavaBlokusThread th) {
        try {
            System.out.println("close " + th);
            th.conn.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
        threads.remove(th);
    }

    private void sendPlayerId(int id) {
        try{
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    conn.getOutputStream())),
                    true); // 送信バッファ設定
            out.println(id);
//            System.out.println("Send playerId : " + id + " to " + this);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void initObj() {
        final int boardSize = JavaBlokusServer.BOARD_SIZE;
        int[][] tmpBoard = new int[boardSize][boardSize];
        String[] players = new String[JavaBlokusServer.PLAYER_NUM];
        boolean[][] tmpAvailablePiece = new boolean[JavaBlokusServer.PLAYER_NUM][JavaBlokusServer.PIECES_NUM];
        for(int i = 0; i < JavaBlokusServer.PLAYER_NUM; i++) {
            players[i] = threads.get(i).playerName;
        }
        comObj.players = players;
        comObj.turn = 0;
        comObj.board = tmpBoard;
        comObj.giveup = false;
        comObj.finished = false;
        comObj.whowon = "nobody";
        comObj.usedPiece = tmpAvailablePiece;
    }

}
