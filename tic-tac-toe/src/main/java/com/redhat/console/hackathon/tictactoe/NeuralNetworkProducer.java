package com.redhat.console.hackathon.tictactoe;

import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import jakarta.enterprise.context.ApplicationScoped;

public class NeuralNetworkProducer {

    @ApplicationScoped
    NeuralNetwork produce() {
        return new NeuralNetwork();
    }
}
