package com.redhat.console.hackathon.neuralnetworks;

import java.util.function.Function;

// https://spotintelligence.com/2023/06/16/activation-function/
public enum ActivationFunction {

    LEAKY_RELU(x -> Math.max(0.01 * x, x)),

    RELU(x -> Math.max(0, x)),

    SIGMOID(x -> 1 / (1 + Math.exp(-x)));

    private final Function<Double, Double> function;

    ActivationFunction(Function<Double, Double> function) {
        this.function = function;
    }

    public Double apply(Double value) {
        return function.apply(value);
    }
}
