package me.primooctopus33.octohack.client.modules.misc;

import java.util.ArrayList;
import java.util.Collections;
import joptsimple.internal.Strings;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PluginsGrabber
extends Module {
    public PluginsGrabber() {
        super("PluginsGrabber", "Attempts to grab and display the plugins installed on a server", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        if (PluginsGrabber.nullCheck()) {
            return;
        }
        PluginsGrabber.mc.player.connection.sendPacket(new CPacketTabComplete("/", null, false));
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            String[] commands;
            SPacketTabComplete packetTabComplete = (SPacketTabComplete)event.getPacket();
            ArrayList<String> plugins = new ArrayList<String>();
            for (String s : commands = packetTabComplete.getMatches()) {
                String pluginName;
                String[] command = s.split(":");
                if (command.length <= 1 || plugins.contains(pluginName = command[0].replace("/", ""))) continue;
                plugins.add(pluginName);
            }
            Collections.sort(plugins);
            if (!plugins.isEmpty()) {
                Command.sendMessage("Plugins \u00a77(\u00a78" + plugins.size() + "\u00a77): \u00a79" + Strings.join(plugins.toArray(new String[0]), "\u00a77, \u00a79"));
            } else {
                Command.sendMessage("Failed to detect Plugins");
            }
            this.disable();
        }
    }
}
