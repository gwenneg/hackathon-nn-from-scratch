package com.redhat.console.hackathon.tictactoe.players;

import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class NeuralNetworkPlayer extends Player {

    @Inject
    NeuralNetwork network;

    @Override
    protected void playImpl() {
        Log.info("Neural network is playing...");
        delayMove();
        networkMove();
        next.play();
    }

    private void networkMove() {

        double[] state = getState();
        double[] predictions = network.feed(state);

        int maxPredictionIndex = 0;
        for (int i = 1; i < predictions.length; i++) {
            if (predictions[i] > predictions[maxPredictionIndex]) {
                maxPredictionIndex = i;
            }
        }

        int x = maxPredictionIndex % 3;
        int y = maxPredictionIndex / 3;

        try {
            mark(x, y);
            engine.sendActivations(network.getActivations());
            network.getActivations().clear();
        } catch (IllegalStateException e) {
            engine.setGameOver(true);
            engine.sendInfo("Neural network made a forbidden move, game was interrupted");
        }
    }

    public double[] getState() {
        Player[][] grid = engine.getGrid();
        double[] state = new double[9];
        int i = 0;
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == null) {
                    state[i++] = 0;
                } else {
                    state[i++] = grid[x][y].getNetworkValue();
                }
            }
        }
        return state;
    }
}
