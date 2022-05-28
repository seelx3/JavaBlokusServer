package javablokusserver;

import java.net.*;
import java.io.*;
import java.util.*;

public class JavaBlokusThread extends Thread {
    protected Socket conn;
    static Vector<JavaBlokusThread> threads;
    String nickname = null;

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
            while (true) {
                String str = in.readLine();
                if (str.equals("thread-size")) {
                    System.out.println("thread-size is " + threads.size());
                }
                if (str.equals("END"))
                    break;
                System.out.println("Echoing : " + str);
                talk(str);
            }
            conn.close();
            System.out.println("Connection Closed");
            threads.remove(this);
        } catch (IOException e) {
            System.err.println(e);
            threads.remove(this);
        }
    }

    public void talk(String message) {
        for (int i = 0; i < threads.size(); i++) {
            JavaBlokusThread t = threads.get(i);
            if (t.isAlive()) {
                t.talkone(t, message);
            }
        }
    }

    public void talkone(JavaBlokusThread talker, String message) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    conn.getOutputStream())),
                    true); // 送信バッファ設定
            out.println(message);
            System.out.println(talker + " " + message);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
