package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvent$Receive
extends PacketEvent {
    public PacketEvent$Receive(int stage, Packet<?> packet) {
        super(stage, packet);
    }
}
