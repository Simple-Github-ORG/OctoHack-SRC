package me.primooctopus33.octohack.client.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class VisualRange
extends Module {
    List<Entity> knownPlayers = new ArrayList<Entity>();
    List<Entity> players;

    public VisualRange() {
        super("VisualRange", "Announces in chat when a player enters your render distance", Module.Category.CHAT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (VisualRange.nullCheck()) {
            return;
        }
        this.players = VisualRange.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
        try {
            for (Entity e2 : this.players) {
                if (!(e2 instanceof EntityPlayer) || e2.getName().equalsIgnoreCase(VisualRange.mc.player.getName()) || this.knownPlayers.contains(e2)) continue;
                this.knownPlayers.add(e2);
                Command.sendMessage(ChatFormatting.WHITE + "<VisualRange> " + ChatFormatting.GRAY + ChatFormatting.RED + e2.getName() + " entered visual range.");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            for (Entity e2 : this.knownPlayers) {
                if (!(e2 instanceof EntityPlayer) || e2.getName().equalsIgnoreCase(VisualRange.mc.player.getName()) || this.players.contains(e2)) continue;
                this.knownPlayers.remove(e2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
