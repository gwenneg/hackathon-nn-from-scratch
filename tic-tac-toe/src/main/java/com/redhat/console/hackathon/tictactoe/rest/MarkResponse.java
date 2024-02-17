package com.redhat.console.hackathon.tictactoe.rest;

import java.util.Optional;

public class MarkResponse {

    private String mark;

    private Integer winner;

    public MarkResponse(String mark, Optional<Integer> winner) {
        this.mark = mark;
        if (winner.isPresent()) {
            this.winner = winner.get();
        }
    }

    public String getMark() {
        return mark;
    }

    public Integer getWinner() {
        return winner;
    }
}
