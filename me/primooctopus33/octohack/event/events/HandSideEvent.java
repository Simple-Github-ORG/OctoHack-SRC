package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;
import net.minecraft.util.EnumHandSide;

public class HandSideEvent
extends EventStage {
    public EnumHandSide handSide;

    public HandSideEvent(EnumHandSide handSide) {
        this.handSide = handSide;
    }

    public EnumHandSide getHandSide() {
        return this.handSide;
    }
}
