package com.redhat.console.hackathon.neuralnetworks;

import java.util.function.Function;

// https://spotintelligence.com/2023/06/16/activation-function/
public enum ActivationFunction {

    LEAKY_RELU(value -> Math.max(0.1 * value, value)),

    RELU(value -> Math.max(0, value)),

    SIGMOID(value -> 1 / (1 + Math.exp(-value)));

    private final Function<Double, Double> function;

    ActivationFunction(Function<Double, Double> function) {
        this.function = function;
    }

    public Double apply(Double value) {
        return function.apply(value);
    }
}
