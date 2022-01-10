package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class MyCode
extends Module {
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Seconds Delay", 7, 1, 20));
    public final Setting<Boolean> packetMessage = this.register(new Setting<Boolean>("Packet Message", false));
    public Timer timer = new Timer();
    public int messageCount = 1;

    public MyCode() {
        super("MyCode", "Spams A Navy Seals CopyPasta edit by me", Module.Category.CHAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.messageCount == 1 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> What the fuck did you just fucking say about that code, you little skidder?"));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> What the fuck did you just fucking say about that code, you little skidder?");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 2 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> I'll have you know I wrote my first fully functional 4D Game when I was 7 years old, and I've been involved in numerous government security projects."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> I'll have you know I wrote my first fully functional 4D Game when I was 7 years old, and I've been involved in numerous government security projects.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 3 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> I have over 300 confirmed private repos on github and am extensively trained in throwing skidders under the bus."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> I have over 300 confirmed private repos on github and am extensively trained in throwing skidders under the bus.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 4 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> I'm the top software developer in the United States. You are nothing to me but just another github repo to fork."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> I'm the top software developer in the United States. You are nothing to me but just another github repo to fork.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 5 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> I will pin you on the front page of my github repos and humiliate you the likes of which has never been seen before on this earth, mark my fucking words."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> I will pin you on the front page of my github repos and humiliate you the likes of which has never been seen before on this earth, mark my fucking words.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 6 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> You think you can get away with stealing that kind of code over the Internet? Think again, fucker."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> You think you can get away with stealing that kind of code over the Internet? Think again, fucker.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 7 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> As we speak I am contacting my secret network of government agents across the United States."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> As we speak I am contacting my secret network of government agents across the United States.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 8 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> They are currently en route to their secret lab ready to deobf the fuck out of your skidded code."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> They are currently en route to their secret lab ready to deobf the fuck out of your skidded code.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 9 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> In other words, your code is fucked, kid. I can deobf anything, anytime, and I can ddos you by computer, laptop, or phone."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> In other words, your code is fucked, kid. I can deobf anything, anytime, and I can ddos you by computer, laptop, or phone.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 10 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> Not only am I extensively trained in ddosing and swatting, but I have access to an entire arsenal of trojan horses and I will use it to its full extent to wipe your miserable skid off the face of this fucking website, you little shit."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> Not only am I extensively trained in ddosing and swatting, but I have access to an entire arsenal of trojan horses and I will use it to its full extent to wipe your miserable skid off the face of this fucking website, you little shit.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 11 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> If only you could have known what unholy retribution your little clever github issue was about to bring down upon you, maybe you wouldn't have pushed the fucking enter button."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> If only you could have known what unholy retribution your little clever github issue was about to bring down upon you, maybe you wouldn't have pushed the fucking enter button.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 12 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> But you couldn't, you didn't, and now you're paying the price, you goddamn paster."));
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> But you couldn't, you didn't, and now you're paying the price, you goddamn paster.");
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
        if (this.messageCount == 13 && this.timer.passedS(this.delay.getValue().intValue())) {
            if (this.packetMessage.getValue().booleanValue()) {
                MyCode.mc.player.connection.sendPacket(new CPacketChatMessage("<MyCode> I will shove binscure transformers up the fucking asshole of your program. You're reputation got fucking owned, kiddo."));
                this.messageCount = 0;
            } else {
                MyCode.mc.player.sendChatMessage("<MyCode> I will shove binscure transformers up the fucking asshole of your program. Your reputation got fucking owned, kiddo.");
                this.messageCount = 0;
            }
            ++this.messageCount;
            this.timer.reset();
            return;
        }
    }
}
