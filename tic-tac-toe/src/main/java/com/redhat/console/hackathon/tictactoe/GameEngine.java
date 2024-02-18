package com.redhat.console.hackathon.tictactoe;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

import static com.redhat.console.hackathon.tictactoe.Player.PLAYER_1;
import static com.redhat.console.hackathon.tictactoe.Player.PLAYER_2;

@ApplicationScoped
public class GameEngine {

    private static final int EMPTY = 0;

    private final int[][] grid = new int[3][3];

    public void reset() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }

    private Player currentPlayer;

    private void updateCurrentPlayer() {
        if (currentPlayer == null || currentPlayer == PLAYER_2) {
            currentPlayer = PLAYER_1;
        } else {
            currentPlayer = PLAYER_2;
        }
    }

    public MarkResult mark(int x, int y) {

        updateCurrentPlayer();

        if (!isValidMove(x, y)) {
            throw new IllegalStateException("Forbidden move");
        }

        grid[x][y] = currentPlayer.getValue();

        return new MarkResult(currentPlayer.getMark(), checkWinner(x, y));
    }

    public double[] getState() {
        double[] state = new double[9];
        int i = 0;
        for (int j = 0; j < grid.length; j++) {
            for (int k = 0; k < grid[0].length; k++) {
                state[i++] = grid[j][k];
            }
        }
        return state;
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3 && grid[x][y] == EMPTY;
    }

    private Optional<Player> checkWinner(int x, int y) {

        if (grid[x][0] != EMPTY && grid[x][0] == grid[x][1] && grid[x][1] == grid[x][2]) {
            return Optional.of(Player.fromValue(grid[x][0]));
        }

        if (grid[0][y] != EMPTY && grid[0][y] == grid[1][y] && grid[1][y] == grid[2][y]) {
            return Optional.of(Player.fromValue(grid[0][y]));
        }

        if (grid[0][0] != EMPTY && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            return Optional.of(Player.fromValue(grid[0][0]));
        }

        if (grid[0][2] != EMPTY && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            return Optional.of(Player.fromValue(grid[0][2]));
        }

        return Optional.empty();
    }
}
