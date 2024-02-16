package com.redhat.console.hackathon.neuralnetworks;

import org.junit.jupiter.api.Test;

public class NeuralNetworkTest {

    @Test
    public void test() {

        NeuralNetwork network = new NeuralNetwork(3, 28, 2, 4, 12, 2);
        network.feed(new double[] {2, 3, 4});
    }
}
