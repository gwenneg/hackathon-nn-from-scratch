package com.redhat.console.hackathon.tictactoe.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class MarkRequest {

    @Min(0)
    @Max(2)
    public int x;

    @Min(0)
    @Max(2)
    public int y;
}
