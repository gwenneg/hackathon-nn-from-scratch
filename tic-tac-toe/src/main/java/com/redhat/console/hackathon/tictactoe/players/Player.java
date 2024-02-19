package com.redhat.console.hackathon.tictactoe.players;

import com.redhat.console.hackathon.tictactoe.GameEngine;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public abstract class Player {

    @ConfigProperty(name = "engine.delay-in-ms", defaultValue = "500")
    int delayInMs;

    @Inject
    GameEngine engine;

    protected Player next;

    private String name;
    private String mark;
    private int networkValue;

    public void play() {
        if (!engine.isGameOver()) {
            playImpl();
        }
    }

    public void setNext(Player next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getNetworkValue() {
        return networkValue;
    }

    public void setNetworkValue(int networkValue) {
        this.networkValue = networkValue;
    }

    protected abstract void playImpl();

    protected void delayMove() {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void mark(int x, int y) {
        engine.mark(x, y, this);
    }
}
