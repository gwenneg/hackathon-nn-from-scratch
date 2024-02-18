package com.redhat.console.hackathon.tictactoe.rest;

import com.redhat.console.hackathon.tictactoe.GameEngine;
import com.redhat.console.hackathon.tictactoe.MarkResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

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
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public MarkResponse mark(MarkRequest request) {
        MarkResult result = engine.mark(request.x, request.y);
        return new MarkResponse(result.getMark(), result.getWinner());
    }
}
