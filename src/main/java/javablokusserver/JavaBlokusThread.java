package javablokusserver;

import java.net.*;
import java.io.*;
import java.util.*;

public class JavaBlokusThread extends Thread {
    protected Socket conn;
    static Vector<JavaBlokusThread> threads;
    static int assignId = 0; // 0 or 1

    static Communication comObj;

    String playerName;
    int playerId;

    static boolean ready = false;


    public JavaBlokusThread(Socket s) {
        conn = s;
        if (threads == null) {
            threads = new Vector<JavaBlokusThread>();
        }
        threads.add(this);
    }

    public void run() {
        try {
            System.out.println("Connected");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream())); // データ受信用バッファの設定

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    conn.getOutputStream())),
                    true); // 送信バッファ設定

            ready = false;

            // プレイヤーの名前を受け取る
            playerName = in.readLine();

            // プレイヤーのIDを渡す
            playerId = assignId;
            out.println(assignId);
            assignId = (assignId + 1) % 2;

            if(threads.size() == 1) InitObj();
            if(threads.size() == 2) ready = true;

            try {
                while (true) {
                    // TODO: 2人目のID渡しが完了したらオブジェクトを送信する
                    if(!ready) {
                        wait(100);
                        continue;
                    }

                    // TODO: オブジェクトをjsonにしてクライアントに送信
                    // 各クライアントに同時にオブジェクトを送信したい
                    out.println(comObj);

                    // TODO: このスレッドのクライアントのターンであれば、クライアントからの応答を待つ

                    wait(100);
                    break;
                }
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }

            conn.close();
            System.out.println("Connection Closed");
            threads.remove(this);
        } catch (IOException e) {
            System.err.println(e);
            threads.remove(this);
        }
    }

    private void InitObj() {
        int[][] tmp = new int[14][14];
        Communication.setCom(0, tmp, false, false, "whowon");
    }

}
