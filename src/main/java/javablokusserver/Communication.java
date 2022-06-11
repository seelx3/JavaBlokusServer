package javablokusserver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Communication {
    public String[] players;
    public int turn;
    public int[][] board;
    public boolean giveup;
    public boolean finished;
    public String whowon;

    public boolean[][] availablePieces;

    @Override
    public String toString() {
        String boardStat = "";
        for (int i = 0; i < JavaBlokusServer.BOARD_SIZE; i++) {
            boardStat += Arrays.toString(board[i]) + "\n";
        }
        return "{\nplayers: " + Arrays.deepToString(players) + "\nturn: " + turn + "\nboard:\n" + boardStat +
                "giveup: " + giveup + "\nfinished: " + finished + "\nwhowon: " + whowon + "\navailablePieces: " +
                Arrays.deepToString(availablePieces) + "\n}\n";
    }

    public void updateTurn() {
        turn = (turn + 1) % JavaBlokusServer.PLAYER_NUM;
    }

}
