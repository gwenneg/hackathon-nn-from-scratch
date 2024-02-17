package com.redhat.console.hackathon.tictactoe;

public enum Player {

    // Will a negative value work well with ReLu? To be confirmed...
    PLAYER_1(-1, "cross"),
    PLAYER_2(1, "circle");

    private final int value;
    private final String mark;

    Player(int value, String mark) {
        this.value = value;
        this.mark = mark;
    }

    public int getValue() {
        return value;
    }

    public String getMark() {
        return mark;
    }
}
