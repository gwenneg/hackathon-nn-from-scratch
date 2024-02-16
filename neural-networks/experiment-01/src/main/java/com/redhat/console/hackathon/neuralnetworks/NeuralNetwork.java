package com.redhat.console.hackathon.neuralnetworks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class NeuralNetwork {

    private List<Layer> layers = new ArrayList<>();

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

    public void exportNetwork() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String serialized = objectMapper.writeValueAsString(layers);

        Files.writeString(Path.of(System.getProperty("user.home") + "/layers_" + LocalDateTime.now() + ".json"), serialized);
    }

    public void importNetwork() throws IOException, URISyntaxException {
        // TODO: Set next and previous from IDs

        Optional<String> json = loadLayersFromResource();
        if (json.isEmpty()) {
            throw new IllegalStateException("Layers file not found");
        }

        List<Layer> layers = new ObjectMapper().readValue(json.get(), new TypeReference<List<Layer>>() {});

        for (Layer layer : layers) {
            if (layer.getPreviousId() != null && layer.getPrevious() == null) {
                layer.setPrevious(layers.get(layer.getPreviousId()));
            }
            if (layer.getNextId() != null && layer.getNext() == null) {
                layer.setNext(layers.get(layer.getNextId()));
            }
        }

        this.layers = layers;
    }

    private static Optional<String> loadLayersFromResource() throws IOException, URISyntaxException {
        URL fileUrl = ClassLoader.getSystemClassLoader().getResource("layers.json");
        if (fileUrl == null) {
            return Optional.empty();
        }
        Path filePath = Paths.get(fileUrl.toURI());
        // May not work well with large files, in case we're using a huge network. Consider replacing with streams and buffered reader.
        return Optional.of(Files.readString(filePath, StandardCharsets.UTF_8));
    }
}
