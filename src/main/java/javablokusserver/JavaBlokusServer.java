package javablokusserver;

import java.net.*;
import java.io.*;
import java.util.*;

import java.io.IOException;

public class JavaBlokusServer {
    final static int PORT = 8090;

    public static void main(String[] args)
        throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started: " + server);
        try {
            Socket conn = null;
            System.out.println("Server Ready");
            while (true) {
                try {
                    conn = server.accept();
                    System.out.println("Connection accepted: " + conn);
                    JavaBlokusThread t = new JavaBlokusThread(conn);
                    t.start();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } finally {
            server.close();
        }
    }
}
