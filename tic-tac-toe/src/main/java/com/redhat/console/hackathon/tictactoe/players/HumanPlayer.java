package com.redhat.console.hackathon.tictactoe.players;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;

@Dependent
public class HumanPlayer extends Player {

    @Override
    protected void playImpl() {
        Log.info("Human is playing...");
        engine.setCurrentPlayer(this);
        // The human player move will come through the WebSocket.
    }

    public void humanMove(int x, int y) {
        mark(x, y);
        next.play();
    }
}
