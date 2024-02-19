package com.redhat.console.hackathon.tictactoe;

import com.redhat.console.hackathon.neuralnetworks.Exporter;
import com.redhat.console.hackathon.neuralnetworks.NeuralNetwork;
import com.redhat.console.hackathon.tictactoe.players.*;
import io.quarkus.runtime.Startup;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Startup
@ApplicationScoped
public class GameEngine {

    public static final String COMMAND = "command";

    private static final String PLAYER_1_NAME = "Player1";
    private static final String PLAYER_2_NAME = "Player2";
    private static final String PLAYER_1_MARK = "cross";
    private static final String PLAYER_2_MARK = "circle";
    private static final int PLAYER_1_NETWORK_VALUE = -1;
    private static final int PLAYER_2_NETWORK_VALUE = 1;

    @Inject
    NeuralNetwork network;

    @Inject
    WebSocket webSocket;

    @Inject
    Instance<Player> players;

    private final Player[][] grid = new Player[3][3];
    private final List<double[]> states = new ArrayList<>();
    private HumanPlayer currentPlayer;
    private boolean gameOver;

    @PostConstruct
    void postConstruct() {
        network.init(9, 20, 20, 9);
    }

    public void setCurrentPlayer(HumanPlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    private Player buildPlayer(PlayerType type, String name, String mark, int networkValue) {
        if (type == null) {
            throw new IllegalArgumentException("Player type must be non null");
        }
        // TODO This will leak if instances are not destroyed when the game is stopped. To be fixed later.
        Player player = switch (type) {
            case HUMAN -> players.select(HumanPlayer.class).get();
            case NEURAL_NETWORK -> players.select(NeuralNetworkPlayer.class).get();
            case DUMB_BOT -> players.select(DumbBotPlayer.class).get();
        };
        player.setName(name);
        player.setMark(mark);
        player.setNetworkValue(networkValue);
        return player;
    }

    public void start(PlayerType playerType1, PlayerType playerType2) {

        for (Player[] row : grid) {
            Arrays.fill(row, null);
        }
        states.clear();
        gameOver = false;

        Player player1 = buildPlayer(playerType1, PLAYER_1_NAME, PLAYER_1_MARK, PLAYER_1_NETWORK_VALUE);
        Player player2 = buildPlayer(playerType2, PLAYER_2_NAME, PLAYER_2_MARK, PLAYER_2_NETWORK_VALUE);
        player1.setNext(player2);
        player2.setNext(player1);

        player1.play();
    }

    private boolean isValidMark(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3 && grid[x][y] == null;
    }

    public void sendState() {

        JsonArray state = new JsonArray();
        for (int x = 0; x < grid.length; x++) {
            JsonArray values = new JsonArray();
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == null) {
                    values.add("");
                } else {
                    values.add(grid[x][y].getMark());
                }
            }
            state.add(values);
        }

        JsonObject payload = new JsonObject();
        payload.put(COMMAND, "state");
        payload.put("state", state);
        webSocket.broadcast(payload);
    }

    public void sendInfo(String info) {
        JsonObject payload = new JsonObject();
        payload.put(COMMAND, "info");
        payload.put("info", info);
        webSocket.broadcast(payload);
    }

    private void sendGameOver(String winner) {
        JsonObject payload = new JsonObject();
        payload.put(COMMAND, "game_over");
        if (winner == null) {
            payload.put("result", "draw");
        } else {
            payload.put("result", "winner");
            payload.put("player", winner);
        }
        webSocket.broadcast(payload);
    }

    public Player[][] getGrid() {
        return grid;
    }

    public void mark(int x, int y, Player player) {

        if (!isValidMark(x, y)) {
            throw new IllegalStateException("Forbidden move");
        }

        grid[x][y] = player;
        sendState();

        Optional<Player> winner = checkWinner(x, y);
        if (winner.isPresent() || isGridFull()) {
            gameOver = true;
            if (winner.isPresent()) {
                sendGameOver(winner.get().getName());
            }
        }
    }

    private Optional<Player> checkWinner(int x, int y) {

        if (grid[x][0] != null && grid[x][0] == grid[x][1] && grid[x][1] == grid[x][2]) {
            return Optional.of(grid[x][0]);
        }

        if (grid[0][y] != null && grid[0][y] == grid[1][y] && grid[1][y] == grid[2][y]) {
            return Optional.of(grid[0][y]);
        }

        if (grid[0][0] != null && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            return Optional.of(grid[0][0]);
        }

        if (grid[0][2] != null && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            return Optional.of(grid[0][2]);
        }

        return Optional.empty();
    }

    private boolean isGridFull() {
        boolean full = true;
        outer: for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == null) {
                    full = false;
                    break outer;
                }
            }
        }
        return full;
    }

    public void markFromWebSocket(int x, int y) {
        currentPlayer.humanMove(x, y);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void export() {
        Exporter.saveToFile(network);
    }
}
