package com.redhat.console.hackathon.tictactoe.rest;

import java.util.Optional;

public class MarkResponse {

    private int winner;

    public MarkResponse(Optional<Integer> winner) {
        if (winner.isPresent()) {
            this.winner = winner.get();
        }
    }

    public int getWinner() {
        return winner;
    }
}
