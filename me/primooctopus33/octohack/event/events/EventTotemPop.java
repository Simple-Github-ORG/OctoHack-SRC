package me.primooctopus33.octohack.event.events;

import java.util.function.Predicate;
import me.primooctopus33.octohack.event.EnumStages;
import me.primooctopus33.octohack.event.OctoHackEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.EntityLivingBase;

public class EventTotemPop
extends OctoHackEvent {
    private final EntityLivingBase entity;
    private final int times;
    @EventHandler
    private final Listener<EventTotemPop> packetRecieveListener = new Listener<EventTotemPop>(event -> {}, new Predicate[0]);

    public EventTotemPop(EnumStages stage, EntityLivingBase entity, int times) {
        super(stage);
        this.entity = entity;
        this.times = times;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public int getTimes() {
        return this.times;
    }
}
