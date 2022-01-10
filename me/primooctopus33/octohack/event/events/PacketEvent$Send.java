package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvent$Send
extends PacketEvent {
    public PacketEvent$Send(int stage, Packet<?> packet) {
        super(stage, packet);
    }
}
