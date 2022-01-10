package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EnumStages;
import me.primooctopus33.octohack.event.OctoHackEvent;
import net.minecraft.network.Packet;

public class EventPacketRecieve
extends OctoHackEvent {
    private final Packet<?> packet;

    public EventPacketRecieve(EnumStages stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}
