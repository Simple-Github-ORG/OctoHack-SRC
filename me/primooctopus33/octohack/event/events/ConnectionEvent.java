package me.primooctopus33.octohack.event.events;

import java.util.UUID;
import me.primooctopus33.octohack.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class ConnectionEvent
extends EventStage {
    private final UUID uuid;
    private final EntityPlayer entity;
    private final String name;

    public ConnectionEvent(int stage, UUID uuid, String name) {
        super(stage);
        this.uuid = uuid;
        this.name = name;
        this.entity = null;
    }

    public ConnectionEvent(int stage, EntityPlayer entity, UUID uuid, String name) {
        super(stage);
        this.entity = entity;
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }
}
