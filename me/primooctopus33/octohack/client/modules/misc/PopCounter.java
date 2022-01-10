package me.primooctopus33.octohack.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.HUD;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class PopCounter
extends Module {
    public final Setting<Boolean> usechat = this.register(new Setting<Boolean>("Send In Chat", false));
    public static HashMap<String, Integer> TotemPopContainer = new HashMap();
    private static PopCounter INSTANCE = new PopCounter();

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.getName())) {
            int l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            if (HUD.getInstance().timestamp.getValue().booleanValue()) {
                if (l_Count == 1) {
                    Command.sendMessage(this.getTimeString() + " " + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem! What a noob");
                    if (this.usechat.getValue().booleanValue()) {
                        PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " died after popping " + l_Count + " Totem! What a noob"));
                    }
                } else {
                    Command.sendMessage(this.getTimeString() + " " + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems! What a noob");
                    if (this.usechat.getValue().booleanValue()) {
                        PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " died after popping " + l_Count + " Totems! What a noob"));
                    }
                }
            } else if (!HUD.getInstance().timestamp.getValue().booleanValue()) {
                if (l_Count == 1) {
                    Command.sendMessage(ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + " Totem! What a noob");
                    if (this.usechat.getValue().booleanValue()) {
                        PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " died after popping " + l_Count + " Totem! What a noob"));
                    }
                } else {
                    Command.sendMessage(ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + " Totems! What a noob");
                    if (this.usechat.getValue().booleanValue()) {
                        PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " died after popping " + l_Count + " Totems! What a noob"));
                    }
                }
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (PopCounter.fullNullCheck()) {
            return;
        }
        if (PopCounter.mc.player.equals(player)) {
            return;
        }
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.put(player.getName(), ++l_Count);
        } else {
            TotemPopContainer.put(player.getName(), l_Count);
        }
        if (HUD.getInstance().timestamp.getValue().booleanValue()) {
            if (l_Count == 1) {
                Command.sendMessage(this.getTimeString() + " " + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem thanks to OctoHack :^)");
                if (this.usechat.getValue().booleanValue()) {
                    PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " popped " + l_Count + " Totem thanks to OctoHack :^)"));
                }
            } else {
                Command.sendMessage(this.getTimeString() + " " + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems thanks to OctoHack :^)");
                if (this.usechat.getValue().booleanValue()) {
                    PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " popped " + l_Count + " Totems thanks to OctoHack :^)"));
                }
            }
        } else if (!HUD.getInstance().timestamp.getValue().booleanValue()) {
            if (l_Count == 1) {
                Command.sendMessage(ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem thanks to OctoHack :^)");
                if (this.usechat.getValue().booleanValue()) {
                    PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " popped " + l_Count + " Totem thanks to OctoHack :^)"));
                }
            } else {
                Command.sendMessage(ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems thanks to OctoHack :^)");
                if (this.usechat.getValue().booleanValue()) {
                    PopCounter.mc.player.connection.sendPacket(new CPacketChatMessage(" " + player.getName() + " popped " + l_Count + " Totems thanks to OctoHack :^)"));
                }
            }
        }
    }
}
