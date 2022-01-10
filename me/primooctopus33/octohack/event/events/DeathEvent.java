package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
extends EventStage {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}
