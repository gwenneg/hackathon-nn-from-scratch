package com.redhat.console.hackathon.tictactoe.rest;

import com.redhat.console.hackathon.tictactoe.Player;

import java.util.Optional;

public class MarkResponse {

    private String mark;

    private Player winner;

    public MarkResponse(String mark, Optional<Player> winner) {
        this.mark = mark;
        if (winner.isPresent()) {
            this.winner = winner.get();
        }
    }

    public String getMark() {
        return mark;
    }

    public Player getWinner() {
        return winner;
    }
}
