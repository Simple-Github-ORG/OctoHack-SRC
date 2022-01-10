package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiLog4j
extends Module {
    public AntiLog4j() {
        super("AntiLog4j", "Stops you from registering text containing what the log4j exploit text would contain", Module.Category.CHAT, true, false, false);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPacketRecieve(PacketEvent.Receive event) {
        String text;
        if (event.getPacket() instanceof SPacketChat && ((text = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText()).contains("${") || text.contains("$<") || text.contains("$:-") || text.contains("jndi:ldap"))) {
            event.setCanceled(true);
        }
    }
}
