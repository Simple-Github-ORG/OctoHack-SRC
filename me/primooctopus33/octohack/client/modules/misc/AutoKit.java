package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoKit
extends Module {
    public final Setting<String> kit = this.register(new Setting<String>("Kit Name", "None"));

    public AutoKit() {
        super("AutoKit", "Automatically gives you a kit when you die", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof GuiGameOver) {
            AutoKit.mc.player.connection.sendPacket(new CPacketChatMessage("/kit " + this.kit.getValue()));
        }
    }
}
