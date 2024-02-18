package com.redhat.console.hackathon.tictactoe.players;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;

import java.security.SecureRandom;
import java.util.*;

@Dependent
public class DumbBotPlayer extends Player {

    private record EmptySquare(int x, int y) {}

    private static final Random secureRandom = new SecureRandom();

    @Override
    protected void playImpl() {
        Log.info("Dumb bot is playing...");
        delayMove();
        randomMove();
        next.play();
    }

    private void randomMove() {

        List<EmptySquare> emptySquares = new ArrayList<>();

        Player[][] grid = engine.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == null) {
                    emptySquares.add(new EmptySquare(x, y));
                }
            }
        }

        EmptySquare square = emptySquares.get(secureRandom.nextInt(0, emptySquares.size()));
        mark(square.x, square.y);
    }
}
