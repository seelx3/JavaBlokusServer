package javablokusserver;

import java.util.ArrayList;
import java.util.Arrays;

public class Communication {
    public String[] players;
    public int turn;
    public int[][] board;
    public boolean giveup;
    public boolean finished;
    public String whowon;

    @Override
    public String toString() {
        return "{\nplayers: " + Arrays.deepToString(players) + "\nturn: " + turn + "\nboard: " + Arrays.deepToString(board) +
                "\ngiveup: " + giveup + "\nfinished: " + finished + "\nwhowon: " + whowon + "\n}\n";
    }

    public void updateTurn() {
        turn = (turn + 1) % JavaBlokusServer.PLAYER_NUM;
    }

}
