package com.redhat.console.hackathon.neuralnetworks;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Exporter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    private static final String DEFAULT_RESOURCE_NAME = "neural-network.json";

    public static void saveToFile(NeuralNetwork network) {
        saveToFile(network, null);
    }

    public static void saveToFile(NeuralNetwork network, Path filePath) {

        if (filePath == null) {
            String userHomeDir = System.getProperty("user.home");
            String fileName = "/neural-network_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".json";
            filePath = Path.of(userHomeDir + fileName);
        }
        try {
            String serializedNetwork = OBJECT_MAPPER.writeValueAsString(network);
            Files.writeString(filePath, serializedNetwork);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static NeuralNetwork loadFromFile() {

        String serializedNetwork = loadNetworkFromResources();

        NeuralNetwork network;
        try {
            network = OBJECT_MAPPER.readValue(serializedNetwork, NeuralNetwork.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        for (Layer layer : network.getLayers()) {
            if (layer.getPreviousId() != null && layer.getPrevious() == null) {
                layer.setPrevious(network.getLayers().get(layer.getPreviousId()));
            }
            if (layer.getNextId() != null && layer.getNext() == null) {
                layer.setNext(network.getLayers().get(layer.getNextId()));
            }
        }

        return network;
    }

    private static String loadNetworkFromResources() {
        return loadNetworkFromResources(null);
    }

    private static String loadNetworkFromResources(String fileName) {

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader == null) {
            throw new IllegalStateException("ClassLoader not found");
        }

        if (fileName == null) {
            fileName = DEFAULT_RESOURCE_NAME;
        }

        URL fileUrl = classLoader.getResource(fileName);
        if (fileUrl == null) {
            throw new IllegalStateException("File not found");
        }

        try {
            Path filePath = Paths.get(fileUrl.toURI());
            // May not work well with large files, in case we're using a huge network. Consider replacing with a stream and a buffered reader.
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
