package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;

public class BlockPushEvent
extends EventStage {
    public double var1;
    public double var2;
    public double var3;

    public BlockPushEvent(double var1, double var2, double var3) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
    }
}
