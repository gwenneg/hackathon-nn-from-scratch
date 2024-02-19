package com.redhat.console.hackathon.neuralnetworks;

import java.util.function.Function;

// https://spotintelligence.com/2023/06/16/activation-function/
public enum ActivationFunction {

    LEAKY_RELU(x -> Math.max(0.01 * x, x), x -> {
        if (x < 0.01 * x)
            return 0.01d;
        return 1d;
    }),

    RELU(x -> Math.max(0, x), x -> {
        if (x <= 0)
            return 0d;
        return 1d;
    }),

    SIGMOID(x -> sigmoid(x), x -> sigmoid(x) * (1 - sigmoid(x)));

    private final Function<Double, Double> function;
    private final Function<Double, Double> derivative;

    ActivationFunction(Function<Double, Double> function, Function<Double, Double> derivative) {
        this.function = function;
        this.derivative = derivative;
    }

    public Double apply(Double value) {
        return function.apply(value);
    }

    public Double applyDerivative(Double value) {
        return derivative.apply(value);
    }
    private static Double sigmoid(Double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
