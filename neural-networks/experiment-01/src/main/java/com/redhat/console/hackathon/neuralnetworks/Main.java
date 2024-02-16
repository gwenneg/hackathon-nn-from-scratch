package com.redhat.console.hackathon.neuralnetworks;

import com.redhat.console.hackathon.neuralnetworks.tictactoe.GameEngine;

public class Main {

    public static void main(String[] args) {

        NeuralNetwork network = new NeuralNetwork();
        network.init(9, 10, 10, 9);

        GameEngine gameEngine = new GameEngine();

        // To be continued...

        double[] networkMove = network.feed(gameEngine.getState());
        // Find the max value, convert it into (x,y), mark that position in the grid
        // If position invalid, "punish" the network and try again?
        // If position valid, "reward" the network? (small reward) or nothing?
        // If opponent victory, "punish" (hard?) the network
        // If network victory, "reward" (greatly?) the network

        // To be continued...
    }
}
