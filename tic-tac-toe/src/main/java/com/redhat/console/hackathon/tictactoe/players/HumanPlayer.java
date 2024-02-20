package com.redhat.console.hackathon.tictactoe.players;

import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import com.redhat.console.hackathon.tictactoe.NeuralNetworkProducer;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class HumanPlayer extends Player {

    @Override
    protected void playImpl() {
        Log.info("Human is playing...");
        engine.setCurrentPlayer(this);
        // The human player move will come through the WebSocket.
    }

    @Inject
    NeuralNetwork network;

    public void humanMove(int x, int y) {
        //Train the network on human moves
        double[] state = getState();
        network.train(state, 0.1, getPredication(x, y));
        mark(x, y);
        next.play();
    }

    private double[] getPredication(int x, int y) {
        double[] prediction = new double[9];
        prediction[x + 3*y] = 1;
        return prediction;
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
                    if (grid[x][y] == this)
                        state[i++] = 1;
                    else
                        state[i++] = -1;
                }
            }
        }
        return state;
    }
}
