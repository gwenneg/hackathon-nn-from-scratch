package com.redhat.console.hackathon.tictactoe;

import java.util.Optional;

public class MarkResult {

    private final String mark;
    private final Optional<Integer> winner;

    public MarkResult(String mark, Optional<Integer> winner) {
        this.mark = mark;
        this.winner = winner;
    }

    public String getMark() {
        return mark;
    }

    public Optional<Integer> getWinner() {
        return winner;
    }
}
