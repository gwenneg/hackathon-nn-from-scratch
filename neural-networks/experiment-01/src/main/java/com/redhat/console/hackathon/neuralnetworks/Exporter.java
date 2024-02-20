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
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

public class Exporter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    private static final String DEFAULT_RESOURCE_NAME = "neural-network.json";

    static {
        OBJECT_MAPPER.enable(INDENT_OUTPUT);
    }

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

        List<Layer> layers = network.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).setNetwork(network);
            Layer current = layers.get(i);
            if (i > 0) {
                Layer previous = layers.get(i - 1);
                current.setPrevious(previous);
            }
            if (i < layers.size() - 1) {
                Layer next = layers.get(i + 1);
                current.setNext(next);
            }
        }

        return network;
    }

    private static String loadNetworkFromResources() {
        return loadNetworkFromResources(null);
    }

    private static String loadNetworkFromResources(String fileName) {

        ClassLoader classLoader = Exporter.class.getClassLoader();
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
