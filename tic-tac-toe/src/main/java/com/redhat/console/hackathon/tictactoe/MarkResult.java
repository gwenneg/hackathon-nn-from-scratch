package com.redhat.console.hackathon.tictactoe;

import java.util.Optional;

public class MarkResult {

    private final String mark;
    private final Optional<Player> winner;

    public MarkResult(String mark, Optional<Player> winner) {
        this.mark = mark;
        this.winner = winner;
    }

    public String getMark() {
        return mark;
    }

    public Optional<Player> getWinner() {
        return winner;
    }
}
