package com.redhat.console.hackathon.tictactoe;

import com.redhat.console.hackathon.neuralnetworks.Exporter;
import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import io.quarkus.logging.Log;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class NeuralNetworkProducer {

    @ConfigProperty(name = "neural-network.load-from-file", defaultValue = "true")
    boolean loadFromFile;

    @Singleton
    NeuralNetwork produce() {
        if (loadFromFile) {
            Log.info("Loading the neural network from a file...");
            return Exporter.loadFromFile();
        } else {
            Log.info("Creating a new neural network...");
            NeuralNetwork network = new NeuralNetwork();
            network.init(9, 15, 11, 9);
            return network;
        }
    }
}
