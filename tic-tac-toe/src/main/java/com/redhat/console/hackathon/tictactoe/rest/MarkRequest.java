package com.redhat.console.hackathon.tictactoe.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class MarkRequest {

    @Min(0)
    @Max(2)
    private int x;

    @Min(0)
    @Max(2)
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
