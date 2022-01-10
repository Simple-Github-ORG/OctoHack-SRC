package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.NoFall;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;

final class NoFall$State$2
extends NoFall.State {
    @Override
    public NoFall.State onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot) {
            return REEQUIP_ELYTRA;
        }
        return this;
    }
}
