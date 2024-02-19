package com.redhat.console.hackathon.neuralnetworks;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class NeuralNetworkTest {

    @Test
    void testNotEnoughLayers() {

        NeuralNetwork network = new NeuralNetwork();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            network.init();
        });
        assertEquals("The network requires at least 3 layers", e.getMessage());

        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> {
            network.init(1);
        });
        assertEquals("The network requires at least 3 layers", e2.getMessage());

        IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () -> {
            network.init(1, 2);
        });
        assertEquals("The network requires at least 3 layers", e3.getMessage());
    }

    @Test
    void testInvalidInputsLength() {
        NeuralNetwork network = new NeuralNetwork();
        network.init(8, 16, 2);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            network.feed(new double[] {2, 3, 4});
        });
        assertEquals("The inputs length has to be the same than the input layer size", e.getMessage());
    }

    @Test
    void test() {

        NeuralNetwork network = new NeuralNetwork();
        network.init(3, 28, 2, 4, 12, 2);

        assertEquals(3, network.getLayers().get(0).getSize());
        assertEquals(28, network.getLayers().get(1).getSize());
        assertEquals(2, network.getLayers().get(2).getSize());
        assertEquals(4, network.getLayers().get(3).getSize());
        assertEquals(12, network.getLayers().get(4).getSize());
        assertEquals(2, network.getLayers().get(5).getSize());

        double[] outputs = network.feed(new double[] {2, 3, 4});

        System.out.println("outputs=" + Arrays.toString(outputs));
        assertEquals(2, outputs.length);

        Exporter.saveToFile(network);

    }

    @Test
    void testSerDes() {

        NeuralNetwork network = new NeuralNetwork();
        System.out.println(network.getLayers().size());
        NeuralNetwork network2 = Exporter.loadFromFile();
        System.out.println(network2.getLayers().size());
        Exporter.saveToFile(network2);
    }

    @Test
    void testSimpleNetworkImprovesWithTraining() {
        NeuralNetwork network = new NeuralNetwork();
        network.init(2, 3, 3, 1);

        double[] inputs = new double[] {0, 1};
        double expected_output = 0.5;
        double naive_output = network.feed(new double[] {0, 1})[0];
        for (int i = 0; i < 100; i++)
            network.train(inputs, 0.1, new double[]{expected_output});

        double trained_output = network.feed(inputs)[0];

        assertTrue(Math.abs(expected_output - trained_output) < Math.abs(expected_output - naive_output));
    }

    @Test
    void testNetworkCanLearnTicTacToe() {
        NeuralNetwork network = new NeuralNetwork();
        network.init(9, 6, 6, 6, 9);

        for (int i = 0; i < 100000; i++) {
            double[] inputs = getRandomizedBoard();
            network.train(inputs, 0.1, getExpectedMoves(inputs));
        }

        double[] board = getRandomizedBoard();
        double[] prediction = network.feed(board);
    }

    private double[] getRandomizedBoard() {
        SecureRandom random = new SecureRandom();
        double[] board = new double[9];
        for (int i = 0; i < board.length; i++) {
            double v = random.nextDouble();
            if (v < 0.33)
                board[i] = -1;
            else if (v > 0.66)
                board[i] = 1;
        }
        return board;
    }
    private double[] getExpectedMoves(double[] board) {
        double[] moves = new double[board.length];
        for (int i = 0; i < board.length; i++)
            if (board[i] == 0)
                moves[i] = 1;
        return moves;
    }
}
