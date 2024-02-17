package com.redhat.console.hackathon.tictactoe.rest;

import com.redhat.console.hackathon.tictactoe.GameEngine;
import com.redhat.console.hackathon.tictactoe.MarkResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/game")
public class GameResource {

    @Inject
    GameEngine engine;

    @PUT
    @Path("/reset")
    public void reset() {
        engine.reset();
    }

    @POST
    @Path("/mark")
    public MarkResponse mark(MarkRequest request) {
        MarkResult result = engine.mark(request.getX(), request.getY());
        return new MarkResponse(result.getMark(), result.getWinner());
    }
}
