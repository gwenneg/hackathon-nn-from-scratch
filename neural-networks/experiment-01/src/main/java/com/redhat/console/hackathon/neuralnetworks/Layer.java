package com.redhat.console.hackathon.neuralnetworks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

// Fields are serialized regardless of their visibility.
@JsonAutoDetect(fieldVisibility = ANY)
// Null fields are not serialized.
@JsonInclude(NON_NULL)
public class Layer {

    private final int size;
    private final ActivationFunction activationFunction;
    private final double[][] weights;
    private final double[] biases;

    @JsonIgnore
    private NeuralNetwork network;
    @JsonIgnore
    private double[] lastActivations;
    @JsonIgnore
    private double[] lastZ;

    @JsonIgnore
    private Layer previous;
    @JsonIgnore
    private Layer next;

    // Needed for deserialization with Jackson.
    public Layer() {
        size = -1;
        activationFunction = null;
        weights = null;
        biases = null;
    }

    public Layer(NeuralNetwork network, Layer previous, int size, ActivationFunction activationFunction) {

        this.network = network;
        this.previous = previous;
        this.size = size;
        this.activationFunction = activationFunction;

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

    public int getSize() {
        return size;
    }

    public void setNetwork(NeuralNetwork network) {
        this.network = network;
    }

    public void setPrevious(Layer previous) {
        this.previous = previous;
    }

    public void setNext(Layer next) {
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
            lastActivations = inputs;
            return next.forward(inputs);
        } else {
            // TODO: Replace with pure Java, but keep this for faster processing?
            // Watch https://www.youtube.com/watch?v=aircAruvnKk&t=821s to understand why we're using matrices multiplication here.
            try (INDArray weightsMatrix = Nd4j.create(weights); INDArray inputsMatrix = Nd4j.create(inputs)) {
                double[] matricesDotProductResult = weightsMatrix.mmul(inputsMatrix).toDoubleVector();
                lastZ = matricesDotProductResult.clone();
                for (int i = 0; i < matricesDotProductResult.length; i++) {
                    double value = matricesDotProductResult[i] + biases[i];
                    matricesDotProductResult[i] = activationFunction.apply(value);
                }
                network.getActivations().add(matricesDotProductResult);
                lastActivations = matricesDotProductResult;
                if (next == null) {
                    return matricesDotProductResult;
                } else {
                    return next.forward(matricesDotProductResult);
                }
            }
        }
    }

    public double[] backward(double[] inputs, double learning_rate, double[] expected_outputs) {
        if (previous == null)
            return next.backward(inputs, learning_rate, expected_outputs);

        if (next == null) {
            double[] dC_da = cost_derivative(expected_outputs, lastActivations);
            double[] da_dz = derivative_activation_wrt_z(lastZ);
            double[] dC_dz = mult(dC_da, da_dz);
            //Adjust biases by dC_dz * learning rate
            for (int i = 0; i < size; i++)
                biases[i] += dC_dz[i] * learning_rate;

            double[] backprop = new double[previous.size]; //backprop is the mean of the dC_dw for all weights from a neuron in the previous layer - essentially dC_da for that neuron
            double[] previousLayerActivations = previous.lastActivations;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < previousLayerActivations.length; j++) {
                    double dC_dw = dC_dz[i] * previousLayerActivations[j];
                    backprop[j] += dC_dw;
                    //Adjust weight by dC_dw * learning rate
                    weights[i][j] += dC_dw * learning_rate;
                }
            }
            for (int i = 0; i < backprop.length; i++)
                backprop[i] /= size;
            return backprop;
        } else {
            double[] dC_da = next.backward(inputs, learning_rate, expected_outputs);
            double[] da_dz = derivative_activation_wrt_z(lastZ);
            double[] dC_dz = mult(dC_da, da_dz);
            //Adjust biases by dC_dz * learning rate
            for (int i = 0; i < size; i++)
                biases[i] += dC_dz[i] * learning_rate;
            double[] backprop = new double[previous.size]; //backprop is the mean of the dC_dw for all weights from a neuron in the previous layer - essentially dC_da for that neuron
            double[] previousLayerActivations = previous.lastActivations;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < previousLayerActivations.length; j++) {
                    double dC_dw = dC_dz[i] * previousLayerActivations[j];
                    backprop[j] += dC_dw;
                    //Adjust weight by dC_dw * learning rate
                    weights[i][j] += dC_dw * learning_rate;
                }
            }
            for (int i = 0; i < backprop.length; i++)
                backprop[i] /= size;
            return backprop;
        }

        /* For each neuron on the next layer:
            * For hidden/output layers
                * Compute the derivative of the weight of each neuron to this layer wrt the cost function
            * For hidden layers
                * Compute the derivative of the bias of each neuron wrt the cost function (via weights on the whole next layer)
            * For the output layer
                * Compute the derivative of the bias of each neuron wrt the cost function
            * Big question: which derivative gets backpropped?
                * derivative of weight = derivative of next activation = derivative of activation_function * derivative of potential
                * Probably return mean of derivatives of cost wrt weights from each node in previous layer (which is the same as derivatives with respect to activation)
                * Could be interesting to capture the derivatives of cost wrt the activation of the input neurons - ie, which inputs the NN is paying attention to
         */
    }



    private void initWeightsAndBiases() {
        double weightBoundary = Math.sqrt(12) / Math.sqrt(size + previous.getSize());
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

    private double[] mult(double[] left, double[] right) {
        if (left.length != right.length)
            throw new IllegalArgumentException(String.format("Cannot multiplay arrays of different lengths. Left: %d, right: %d", left.length, right.length));

        double[] result = new double[left.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = left[i] * right[i];
        }
        return result;
    }
    private double[] cost_derivative(double[] expected_output, double[] actual_output) {
        double[] derivative = new double[expected_output.length];

        for (int i = 0; i < expected_output.length; i++)
            derivative[i] = 2 * (expected_output[i]-actual_output[i]);

        return derivative;
    }

    private double[] derivative_activation_wrt_z(double[] z) {
        double[] derivative = new double[z.length];
        for (int i = 0; i < z.length; i++)
            derivative[i] = activationFunction.applyDerivative(z[i]);
        return derivative;
    }
}
