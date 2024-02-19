package com.redhat.console.hackathon.neuralnetworks;

import java.util.*;

public class NeuralNetwork {

    private final List<Layer> layers = new ArrayList<>();
    private final List<double[]> activations = new ArrayList<>();

    public List<Layer> getLayers() {
        return layers;
    }

    public void init(int... layersSize) {

        if (layersSize.length < 3) {
            throw new IllegalArgumentException("The network requires at least 3 layers");
        }

        for (int i = 0; i < layersSize.length; i++) {
            if (i == 0) {
                Layer inputLayer = new Layer(this, null, layersSize[i], null);
                layers.add(inputLayer);
            } else if (i < layersSize.length - 1) {
                Layer hiddenLayer = new Layer(this, layers.get(i - 1), layersSize[i], ActivationFunction.RELU);
                layers.add(hiddenLayer);
            } else {
                Layer outputLayer = new Layer(this, layers.get(i - 1), layersSize[i], ActivationFunction.SIGMOID);
                layers.add(outputLayer);
            }
        }
    }

    public double[] feed(double[] inputs) {
        return layers.get(0).forward(inputs);
    }

    public List<double[]> getActivations() {
        return activations;
    }
}
