package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent
extends EventStage {
    private EntityPlayer entity;
    private final int entId;

    public TotemPopEvent(EntityPlayer entity) {
        this.entity = entity;
        this.entId = this.getEntityId();
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }

    public int getEntityId() {
        return this.entId;
    }
}
