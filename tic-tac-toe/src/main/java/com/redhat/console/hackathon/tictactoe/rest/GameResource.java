package com.redhat.console.hackathon.tictactoe.rest;

import com.redhat.console.hackathon.tictactoe.GameEngine;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import java.util.Optional;

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
        Optional<Integer> winner = engine.mark(request.getX(), request.getY(), request.getPlayer());
        return new MarkResponse(winner);
    }
}
