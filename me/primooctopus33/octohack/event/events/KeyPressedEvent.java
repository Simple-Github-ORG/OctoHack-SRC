package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;

public class KeyPressedEvent
extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyPressedEvent(boolean info, boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}
