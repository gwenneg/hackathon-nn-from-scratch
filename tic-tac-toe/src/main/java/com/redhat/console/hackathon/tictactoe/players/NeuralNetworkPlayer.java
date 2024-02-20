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

        int x, y, tries = 0;
        double lessthan = Double.MAX_VALUE;
        do {
            int maxPredictionIndex = findValidMoveWithGreatestPrediction(predictions, lessthan);

            x = maxPredictionIndex % 3;
            y = maxPredictionIndex / 3;
            tries++;
            lessthan = predictions[maxPredictionIndex];
            Log.infof("Guessed the %dth item", maxPredictionIndex);
        } while (!engine.isValidMark(x, y) && tries < 20);
        if (tries > 1) {
            //Train it to favor the backup option that was selected with a higher learning rate the more tries it took
            double[] expected = getPredication(x, y);
            network.train(state, 0.1 * tries, expected);
            Log.infof("Took %d tries to find a valid move.", tries);
        }

        try {
            mark(x, y);
            engine.sendActivations(network.getActivations());
            network.getActivations().clear();
        } catch (IllegalStateException e) {
            engine.setGameOver(true);
            engine.sendInfo("Neural network made a forbidden move, game was interrupted");
        }
    }

    private int findValidMoveWithGreatestPrediction(double[] predictions, double lessthan) {
        int maxIndex = 0;
        double maxValue = 0;
        for (int i = 0; i < predictions.length; i++) { //Changed this to run from 0 because, if the 0th item were greater than the 'lessthan' value, it would skip valid entries

            if (predictions[i] >= lessthan) {
                Log.infof("Skipping value %e because it's greater than previous attempt %e", predictions[i], lessthan);
                continue;
            }
            Log.infof("Candidate %e is greater than sentinel value %e", predictions[i], lessthan);

            if (predictions[i] > maxValue) {
                Log.infof("Updating candidate to %e because it's greater than previous candidate %e", predictions[i], maxValue);
                maxIndex = i;
                maxValue = predictions[i];
            }
        }

        return maxIndex;
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

    private double[] getPredication(int x, int y) {
        double[] prediction = new double[9];
        prediction[x + 3*y] = 1;
        return prediction;
    }
}
