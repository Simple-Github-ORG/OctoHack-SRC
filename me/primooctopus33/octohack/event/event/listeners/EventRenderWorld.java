package me.primooctopus33.octohack.event.event.listeners;

import me.primooctopus33.octohack.event.event.Event;

public class EventRenderWorld
extends Event<EventRenderWorld> {
    float partialTicks;

    public EventRenderWorld(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
