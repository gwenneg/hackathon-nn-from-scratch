package com.redhat.console.hackathon.neuralnetworks;

public class ActivationFunction {

    public static double relu(double value) {
        return Math.max(0, value);
    }

    public static double leakyRelu(double value) {
        return Math.max(0.1 * value, value);
    }

    public static double sigmoid(double value) {
        return 1 / (1 + Math.exp(-value));
    }

    public static double softmax(double value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
