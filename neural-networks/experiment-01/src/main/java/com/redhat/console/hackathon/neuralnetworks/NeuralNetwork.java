package com.redhat.console.hackathon.neuralnetworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralNetwork {

    private final List<Layer> layers = new ArrayList<>();

    public NeuralNetwork(int... layersSize) {
        for (int i = 0; i < layersSize.length; i++) {
            if (i == 0) {
                Layer inputLayer = new Layer(null, layersSize[i], null);
                layers.add(inputLayer);
            } else if (i < layersSize.length - 1) {
                Layer hiddenLayer = new Layer(layers.get(i - 1), layersSize[i], ActivationType.RELU);
                layers.add(hiddenLayer);
            } else {
                Layer outputLayer = new Layer(layers.get(i - 1), layersSize[i], ActivationType.SIGMOID);
                layers.add(outputLayer);
            }
        }
    }

    public void feed(double[] inputs) {
        double[] outputs = layers.get(0).forward(inputs);
        System.out.println("outputs=" + Arrays.toString(outputs));
    }
}
