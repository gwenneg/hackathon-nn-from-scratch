package com.redhat.console.hackathon.tictactoe;

import com.redhat.console.hackathon.tictactoe.players.PlayerType;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.redhat.console.hackathon.tictactoe.GameEngine.COMMAND;

@ApplicationScoped
@ServerEndpoint("/tic-tac-toe")
public class WebSocket {

    @Inject
    GameEngine engine;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    void onOpen(Session session) {
        sessions.put(session.getId(), session);
        Log.infof("Session %s opened", session.getId());
        engine.sendState();
    }

    @OnClose
    void onClose(Session session) {
        sessions.remove(session.getId());
        Log.infof("Session %s closed", session.getId());
    }

    @OnError
    void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        if (throwable != null) {
            Log.errorf(throwable, "Session %s closed because of an error", session.getId());
        }
    }

    @OnMessage
    void onMessage(String message) {
        JsonObject payload = new JsonObject(message);
        String command = payload.getString(COMMAND);
        if (command == null) {
            Log.errorf("Invalid message: %s", message);
        } else {
            switch (command) {
                case "start" -> {
                    PlayerType playerType1 = PlayerType.valueOf(payload.getString("playerType1"));
                    PlayerType playerType2 = PlayerType.valueOf(payload.getString("playerType2"));
                    engine.start(playerType1, playerType2);
                }
                case "mark" -> {
                    int x = payload.getInteger("x");
                    int y = payload.getInteger("y");
                    engine.markFromWebSocket(x, y);
                }
                case "stop" -> {
                    engine.stop();
                }
                default -> {
                    Log.warnf("Unexpected command: %s", command);
                }
            }
        }
    }

    public void broadcast(JsonObject payload) {
        String message = payload.encode();
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    Log.errorf("Unable to send message: %s", message);
                }
            });
        });
    }
}
