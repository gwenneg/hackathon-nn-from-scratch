package com.redhat.console.hackathon.neuralnetworks;

import java.util.*;

public class NeuralNetwork {

    private final List<Layer> layers = new ArrayList<>();

    public void init(int... layersSize) {

        if (layersSize.length < 3) {
            throw new IllegalArgumentException("The network requires at least 3 layers");
        }

        for (int i = 0; i < layersSize.length; i++) {
            if (i == 0) {
                Layer inputLayer = new Layer(i, null, layersSize[i], null);
                layers.add(inputLayer);
            } else if (i < layersSize.length - 1) {
                Layer hiddenLayer = new Layer(i, layers.get(i - 1), layersSize[i], ActivationType.RELU);
                layers.add(hiddenLayer);
            } else {
                Layer outputLayer = new Layer(i, layers.get(i - 1), layersSize[i], ActivationType.SIGMOID);
                layers.add(outputLayer);
            }
        }
    }

    public double[] feed(double[] inputs) {
        return layers.get(0).forward(inputs);
    }

    public List<Layer> getLayers() {
        return layers;
    }
}
