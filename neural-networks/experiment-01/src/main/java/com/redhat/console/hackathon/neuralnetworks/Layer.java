package com.redhat.console.hackathon.neuralnetworks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Layer {

    // Required for (de)serialization purposes.
    private final int id;
    // Integer is preferred over int here because it can be null.
    private Integer previousId;
    private Integer nextId;
    private final int size;
    private final ActivationType activationType;
    private final double[][] weights;
    private final double[] biases;

    @JsonIgnore
    private Layer previous;
    @JsonIgnore
    private Layer next;

    // Needed for deserialization with Jackson.
    public Layer() {
        id = -1;
        previous = null;
        size = 0;
        activationType = null;
        weights = null;
        biases = null;
    }

    public Layer(int id, Layer previous, int size, ActivationType activationType) {

        this.id = id;
        this.previous = previous;
        if (previous != null) {
            this.previousId = previous.getId();
        }
        this.size = size;
        this.activationType = activationType;

        // The input layer has no weights or biases.
        if (previous != null) {
            previous.setNext(this);
            weights = new double[size][previous.getSize()];
            biases = new double[size];
            initWeightsAndBiases();
        } else {
            weights = null;
            biases = null;
        }
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public void setNext(Layer next) {
        this.nextId = next.getId();
        this.next = next;
    }

    public double[] forward(double[] inputs) {

        if (previous == null && next == null) {
            throw new IllegalStateException("A layer needs a previous, a next or both");
        }

        System.out.println("inputs=" + Arrays.toString(inputs));

        // This layer is the input layer of the network.
        if (previous == null) {
            if (inputs.length != size) {
                throw new IllegalArgumentException("The inputs length has to be the same than the input layer size");
            }
            return next.forward(inputs);
        } else {
            // TODO: Replace with pure Java, but keep this for faster processing?
            // Watch https://www.youtube.com/watch?v=aircAruvnKk&t=821s to understand why we're using matrices multiplication here.
            try (INDArray weightsMatrix = Nd4j.create(weights); INDArray inputsMatrix = Nd4j.create(inputs)) {
                double[] matricesDotProductResult = weightsMatrix.mmul(inputsMatrix).toDoubleVector();
                for (int i = 0; i < matricesDotProductResult.length; i++) {
                    double value = matricesDotProductResult[i] + biases[i];
                    switch (activationType) {
                        case RELU -> matricesDotProductResult[i] = ActivationFunction.relu(value);
                        case SIGMOID -> matricesDotProductResult[i] = ActivationFunction.sigmoid(value);
                    }
                }
                if (next == null) {
                    return matricesDotProductResult;
                } else {
                    return next.forward(matricesDotProductResult);
                }
            }
        }
    }

    public Layer getPrevious() {
        return previous;
    }

    public Integer getPreviousId() {
        return previousId;
    }

    public Integer getNextId() {
        return nextId;
    }

    public Layer getNext() {
        return next;
    }

    private void initWeightsAndBiases() {
        double weightBoundary = Math.sqrt(6) / Math.sqrt(size + previous.getSize());
        Random random = new SecureRandom();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < previous.getSize(); j++) {
                // https://machinelearningmastery.com/weight-initialization-for-deep-learning-neural-networks/
                weights[i][j] = random.nextDouble(-weightBoundary, weightBoundary);
            }
            // https://medium.com/@glenmeyerowitz/bias-initialization-in-a-neural-network-2e5d26fed0f0
            biases[i] = 0;
        }
        System.out.println("weights=" + Arrays.deepToString(weights));
        System.out.println("biases=" + Arrays.toString(biases));
    }

    public void setPrevious(Layer previous) {
        this.previous = previous;
    }
}
