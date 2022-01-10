package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class AutoCope
extends Module {
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Seconds Delay", 7, 1, 20));
    public final Setting<Boolean> packetMessage = this.register(new Setting<Boolean>("Packet Message", false));
    public Timer timer = new Timer();
    public int messageCount = 1;

    public AutoCope() {
        super("AutoCope", "Automatically makes you cope in chat", Module.Category.CHAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.messageCount == 1 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> Why do I keep getting ratted?"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> Why do I keep getting ratted?");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 2 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> I keep mining bedrock thinking its obsidian, please help me"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> I keep mining bedrock thinking its obsidian, please help me");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 3 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> Why doesnt impact fly work????"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> Why doesnt impact fly work????");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 4 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> Stop griefing my build pls bro i beg what did i ever do to u!!!"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> Stop griefing my build pls bro i beg what did i ever do to u!!!");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 5 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> I have huge iq issue, please help me"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> I have huge iq issue, please help me");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 6 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> I have skill issue, how to play?????????????????"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> I have skill issue, how to play?????????????????");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 7 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> I got Rubberbanded, I got Kicked, I Crashed, my Fps Dropped, my Totem Failed, my Ping Spiked, pls help."));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> I got Rubberbanded, I got Kicked, I Crashed, my Fps Dropped, my Totem Failed, my Ping Spiked, pls help.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 8 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> How to escape spawn???"));
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> How to escape spawn???");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 9 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoCope.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoCope> Im coping and seething because i got 10v1'd in block game, i have mental issues please help"));
                this.messageCount = 0;
            } else {
                AutoCope.mc.player.sendChatMessage("<AutoCope> Im coping and seething because i got 10v1'd in block game, i have mental issues please help");
                this.messageCount = 0;
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
    }
}
