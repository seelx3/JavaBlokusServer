package javablokusserver;

import java.util.Arrays;

public class Communication {
    static int turn;
    static int[][] board;
    static boolean giveup;
    static boolean finished;
    static String whowon;

    public static void setCom(int tn, int[][] bd, boolean gu, boolean fin, String ww){
        turn = tn;
        board = bd;
        giveup = gu;
        finished = fin;
        whowon = ww;
    }

    @Override
    public String toString() {
        return "{\nturn: " + turn + "\nboard: " + Arrays.deepToString(board) +
                "\ngiveup: " + giveup + "\nfinished: " + finished + "\nwhowon: " + whowon + "\n}\n";
    }
}
