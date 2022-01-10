package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate
extends Module {
    public NoRotate() {
        super("NoRotate", "Stops you from processing server rotations", Module.Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            try {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
                packet.yaw = NoRotate.mc.player.rotationYaw;
                packet.pitch = NoRotate.mc.player.rotationPitch;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}
