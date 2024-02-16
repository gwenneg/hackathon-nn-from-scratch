package com.redhat.console.hackathon.neuralnetworks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NeuralNetworkTest {

    @Test
    void testNotEnoughLayers() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new NeuralNetwork();
        });
        assertEquals("The network requires at least 3 layers", e.getMessage());

        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> {
            new NeuralNetwork(1);
        });
        assertEquals("The network requires at least 3 layers", e2.getMessage());

        IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () -> {
            new NeuralNetwork(1, 2);
        });
        assertEquals("The network requires at least 3 layers", e3.getMessage());
    }

    @Test
    void testInvalidInputsLength() {
        NeuralNetwork network = new NeuralNetwork(8, 16, 2);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            network.feed(new double[] {2, 3, 4});
        });
        assertEquals("The inputs length has to be the same than the input layer size", e.getMessage());
    }

    @Test
    void test() {

        NeuralNetwork network = new NeuralNetwork(3, 28, 2, 4, 12, 2);

        assertEquals(3, network.getLayers().get(0).getSize());
        assertEquals(28, network.getLayers().get(1).getSize());
        assertEquals(2, network.getLayers().get(2).getSize());
        assertEquals(4, network.getLayers().get(3).getSize());
        assertEquals(12, network.getLayers().get(4).getSize());
        assertEquals(2, network.getLayers().get(5).getSize());

        double[] outputs = network.feed(new double[] {2, 3, 4});

        System.out.println("outputs=" + Arrays.toString(outputs));
        assertEquals(2, outputs.length);

    }
}
