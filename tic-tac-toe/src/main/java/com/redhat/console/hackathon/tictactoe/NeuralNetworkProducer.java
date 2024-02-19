package com.redhat.console.hackathon.tictactoe;

import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import jakarta.inject.Singleton;

public class NeuralNetworkProducer {

    @Singleton
    NeuralNetwork produce() {
        return new NeuralNetwork();
    }
}
