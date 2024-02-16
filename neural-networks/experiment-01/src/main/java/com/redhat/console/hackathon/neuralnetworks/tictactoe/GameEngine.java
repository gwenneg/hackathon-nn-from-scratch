package com.redhat.console.hackathon.neuralnetworks.tictactoe;

import java.util.Optional;

public class GameEngine {

    private static final int EMPTY = 0;

    // Will a negative value work well with ReLu? To be confirmed...
    private static final int PLAYER1 = -1;
    private static final int PLAYER2 = 1;

    private final int[][] grid = new int[3][3];

    public void mark(int x, int y, int player) {

        if (!isValidMove(x, y, player)) {
            throw new IllegalStateException("Forbidden move");
        }

        grid[x][y] = player;

        Optional<Integer> winner = checkWinner(x, y);

        // winner has won!

    }

    private boolean isValidMove(int x, int y, int player) {
        return x >= 0 && x < 3 && y >= 0 && y < 3 && grid[x][y] == EMPTY && player == PLAYER1 || player == PLAYER2;
    }

    private Optional<Integer> checkWinner(int x, int y) {

        if (grid[x][0] == grid[x][1] && grid[x][1] == grid[x][2]) {
            return Optional.of(grid[x][0]);
        }

        if (grid[0][y] == grid[1][y] && grid[1][y] == grid[2][y]) {
            return Optional.of(grid[0][y]);
        }

        if (grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            return Optional.of(grid[0][0]);
        }

        if (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            return Optional.of(grid[0][2]);
        }

        return Optional.empty();
    }
}
