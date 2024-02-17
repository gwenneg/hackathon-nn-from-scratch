package com.redhat.console.hackathon.tictactoe.rest;

import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/network")
public class NeuralNetworkResource {

    @Inject
    NeuralNetwork network;

    @POST
    @Path("/init")
    public void init(List<Integer> layersSize) {
        // TODO To be implemented correctly...
        network.init();
    }
}
