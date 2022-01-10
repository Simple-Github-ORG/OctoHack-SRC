package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;

public class KeyEvent
extends EventStage {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }
}
