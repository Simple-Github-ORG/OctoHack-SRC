package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;

public class UpdateEvent
extends EventStage {
    private final int stage;

    public UpdateEvent(int stage) {
        this.stage = stage;
    }

    @Override
    public final int getStage() {
        return this.stage;
    }
}
