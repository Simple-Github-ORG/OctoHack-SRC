package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class AutoFitFag
extends Module {
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Seconds Delay", 7, 1, 20));
    public final Setting<Boolean> packetMessage = this.register(new Setting<Boolean>("Packet Message", false));
    public Timer timer = new Timer();
    public int messageCount = 1;

    public AutoFitFag() {
        super("AutoFitFag", "Automatically makes you spam things from fit's videos", Module.Category.CHAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.messageCount == 1 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> 2b2t is the oldest anarchy server in all of minecraft."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> 2b2t is the oldest anarchy server in all of minecraft.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 2 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> 2b2t is not only the oldest anarchy server in minecraft, it is also one of the oldest servers in minecraft."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> 2b2t is not only the oldest anarchy server in minecraft, it is also one of the oldest servers in minecraft.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 3 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Players come and go on the oldest anarchy server in minecraft. But there is 1 player, who left their mark on the server forever. That player's name, is popbob"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Players come and go on the oldest anarchy server in minecraft. But there is 1 player, who left their mark on the server forever. That player's name, is popbob");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 4 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> popbob has griefed more bases and monuments on the server than any other player."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> popbob has griefed more bases and monuments on the server than any other player.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 5 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> At one point, just seeing popbob was a death sentence"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> At one point, just seeing popbob was a death sentence");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 6 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Kingoros is one of the deadliest vanilla pvpers on 2b2t."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Kingoros is one of the deadliest vanilla pvpers on 2b2t.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 7 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> From 2010 - 2018 only 30 players have ever held a 32k weapon. Less than half of these players have ever used a 32k weapon in battle."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> From 2010 - 2018 only 30 players have ever held a 32k weapon. Less than half of these players have ever used a 32k weapon in battle.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 8 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> The dragon egg was used on the oldest anarchy server in minecraft to break bedrock."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> The dragon egg was used on the oldest anarchy server in minecraft to break bedrock.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 9 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> 2b2t has the longest nether highways ever built in survival minecraft."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> 2b2t has the longest nether highways ever built in survival minecraft.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 10 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Furnaces were used on 2b2t in order to chunkban players."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Furnaces were used on 2b2t in order to chunkban players.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 11 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Did you know there is an underground fight club on the oldest anarchy server in minecraft?"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Did you know there is an underground fight club on the oldest anarchy server in minecraft?");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 12 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> 2b2t players are trying to destroy trees so new players can't get wood easily"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> 2b2t players are trying to destroy trees so new players can't get wood easily");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 13 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Many people have been banned from purchasing priority queue on 2b2t"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Many people have been banned from purchasing priority queue on 2b2t");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 14 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Watercubes are being used on 2b2t to protect builds of historical significance."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Watercubes are being used on 2b2t to protect builds of historical significance.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 15 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> itristan's backdoor books are the rarest things on 2b2t"));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> itristan's backdoor books are the rarest things on 2b2t");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 16 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> 2b2t was never a real anarchy server."));
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> 2b2t was never a real anarchy server.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 17 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoFitFag.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoFitFag> Thats all for today FitFam, take it easy and if you plan to play, make sure to stay alive out there."));
                this.messageCount = 0;
            } else {
                AutoFitFag.mc.player.sendChatMessage("<AutoFitFag> Thats all for today FitFam, take it easy and if you plan to play, make sure to stay alive out there.");
                this.messageCount = 0;
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
    }
}
