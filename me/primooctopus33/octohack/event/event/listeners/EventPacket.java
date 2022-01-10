package me.primooctopus33.octohack.event.event.listeners;

import me.primooctopus33.octohack.event.event.Event;
import net.minecraft.network.Packet;

public class EventPacket
extends Event<EventPacket> {
    Packet packet;

    public EventPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
