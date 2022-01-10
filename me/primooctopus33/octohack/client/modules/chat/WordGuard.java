package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.TextUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WordGuard
extends Module {
    public final Setting<Boolean> packetMessage = this.register(new Setting<Boolean>("Packet Message", true));
    public final Setting<Integer> afterMessageDelay = this.register(new Setting<Integer>("Delay Seconds", 2, 1, 10));
    public final Setting<Integer> bypassCharacterAmount = this.register(new Setting<Integer>("Bypass Character Amount", 4, 1, 15));
    public final Setting<Boolean> detectHacking = this.register(new Setting<Boolean>("Detect Cheats", true));
    public final Setting<Boolean> detectThreats = this.register(new Setting<Boolean>("Detect Threats", true));
    public final Setting<Boolean> detectSwearing = this.register(new Setting<Boolean>("Detect Swearing", true));
    public Timer timer = new Timer();

    public WordGuard() {
        super("WordGuard", "Automatically sends Hey! Sorry, but _______ is not allowed here!", Module.Category.CHAT, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (WordGuard.fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            String message = packet.getChatComponent().getFormattedText();
            if (this.detectSwearing.getValue().booleanValue() && (message.contains("fuck") || message.contains("fucking") || message.contains("bastard") || message.contains("shit") || message.contains("ass") || message.contains("shitty") || message.contains("faggot") || message.contains("shitter") || message.contains("shitbox") || message.contains("cunt") || message.contains("fucked") || message.contains("rape") || message.contains("raped") || message.contains("nigger") || message.contains("nigga") || message.contains("shitass") || message.contains("bitch") || message.contains("retard") || message.contains("retarded") || message.contains("fag") || message.contains("gay") || message.contains("incel")) && this.timer.passedS(this.afterMessageDelay.getValue().intValue())) {
                if (this.packetMessage.getValue().booleanValue()) {
                    WordGuard.mc.player.connection.sendPacket(new CPacketChatMessage("<WordGuard> Hey! Sorry, but swearing is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue())));
                    this.timer.reset();
                } else {
                    WordGuard.mc.player.sendChatMessage("<WordGuard> Hey! Sorry, but swearing is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue()));
                    this.timer.reset();
                }
            }
            if (this.detectThreats.getValue().booleanValue() && (message.contains("swat") || message.contains("ddos") || message.contains("kys")) && this.timer.passedS(this.afterMessageDelay.getValue().intValue())) {
                if (this.packetMessage.getValue().booleanValue()) {
                    WordGuard.mc.player.connection.sendPacket(new CPacketChatMessage("<WordGuard> Hey! Sorry, but that word is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue())));
                    this.timer.reset();
                } else {
                    WordGuard.mc.player.sendChatMessage("<WordGuard> Hey! Sorry, but that word is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue()));
                    this.timer.reset();
                }
            }
            if (this.detectHacking.getValue().booleanValue() && (message.contains("hack") || message.contains("cheat") || message.contains("exploit")) && this.timer.passedS(this.afterMessageDelay.getValue().intValue())) {
                if (this.packetMessage.getValue().booleanValue()) {
                    WordGuard.mc.player.connection.sendPacket(new CPacketChatMessage("<WordGuard> Hey! Sorry, but cheating is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue())));
                    this.timer.reset();
                } else {
                    WordGuard.mc.player.sendChatMessage("<WordGuard> Hey! Sorry, but cheating is not allowed here! " + TextUtil.generateRandomHexSuffix(this.bypassCharacterAmount.getValue()));
                    this.timer.reset();
                }
            }
        }
    }
}
