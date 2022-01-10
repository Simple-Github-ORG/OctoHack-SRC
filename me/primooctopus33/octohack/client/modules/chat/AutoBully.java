package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class AutoBully
extends Module {
    public final Setting<Amount> amount = this.register(new Setting<Amount>("Amount", Amount.Once));
    public final Setting<Message> message = this.register(new Setting<Message>("Message", Message.HelloFan));
    public final Setting<Boolean> packetMessage = this.register(new Setting<Boolean>("Packet Message", true));
    public final Setting<Boolean> announcePlayer = this.register(new Setting<Boolean>("Announce Target", false));
    public final Setting<String> player = this.register(new Setting<String>("Player", "None"));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Seconds Delay", 7, 0, 50));
    public final Timer timer = new Timer();
    public boolean notAnnounced = true;

    public AutoBully() {
        super("AutoBully", "Automatically bullies other people in spanish", Module.Category.CHAT, true, false, false);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void onUpdate() {
        if (this.announcePlayer.getValue().booleanValue() && this.notAnnounced) {
            if (this.packetMessage.getValue().booleanValue()) {
                AutoBully.mc.player.connection.sendPacket(new CPacketChatMessage("<AutoBully> Now Bullying " + this.player.getValue()));
                this.notAnnounced = false;
                return;
            }
            AutoBully.mc.player.sendChatMessage("<AutoBully> Now Bullying" + this.player.getValue());
            this.notAnnounced = false;
            return;
        }
        switch (1.$SwitchMap$me$primooctopus33$octohack$client$modules$chat$AutoBully$Message[this.message.getValue().ordinal()]) {
            case 1: {
                if (this.amount.getValue() != Amount.Once) ** GOTO lbl19
                if (this.packetMessage.getValue().booleanValue()) {
                    AutoBully.mc.player.connection.sendPacket(new CPacketChatMessage("/msg " + this.player.getValue() + " <AutoBully> hola faNN como estas? para cuando aprendes pvp? mueres en 2v1 siquiera puedes matar a 1, ah y otra cosa, tu clan no existe literal, no conozco a nadie aparte de ti que sea de JotaWho"));
                    this.disable();
                } else {
                    AutoBully.mc.player.sendChatMessage("/msg " + this.player.getValue() + " <AutoBully> hola faNN como estas? para cuando aprendes pvp? mueres en 2v1 siquiera puedes matar a 1, ah y otra cosa, tu clan no existe literal, no conozco a nadie aparte de ti que sea de JotaWho");
                    this.disable();
                }
                ** GOTO lbl28
lbl19:
                // 1 sources

                if (this.amount.getValue() == Amount.Continual && this.timer.passedS(this.delay.getValue().intValue())) {
                    if (this.packetMessage.getValue().booleanValue()) {
                        AutoBully.mc.player.connection.sendPacket(new CPacketChatMessage("/msg " + this.player.getValue() + " <AutoBully> hola faNN como estas? para cuando aprendes pvp? mueres en 2v1 siquiera puedes matar a 1, ah y otra cosa, tu clan no existe literal, no conozco a nadie aparte de ti que sea de JotaWho"));
                        this.timer.reset();
                    } else {
                        AutoBully.mc.player.sendChatMessage("/msg " + this.player.getValue() + " <AutoBully> hola faNN como estas? para cuando aprendes pvp? mueres en 2v1 siquiera puedes matar a 1, ah y otra cosa, tu clan no existe literal, no conozco a nadie aparte de ti que sea de JotaWho");
                        this.timer.reset();
                    }
                }
            }
lbl28:
            // 7 sources

            case 2: {
                if (this.amount.getValue() == Amount.Once) {
                    if (this.packetMessage.getValue().booleanValue()) {
                        AutoBully.mc.player.connection.sendPacket(new CPacketChatMessage("/msg " + this.player.getValue() + " <AutoBully> Hola perro hijueputa, cuando aprendes pvp? tonto culiao"));
                        this.disable();
                        break;
                    }
                    AutoBully.mc.player.sendChatMessage("/msg " + this.player.getValue() + " <AutoBully> Hola perro hijueputa, cuando aprendes pvp? tonto culiao");
                    this.disable();
                    break;
                }
                if (this.amount.getValue() != Amount.Continual || !this.timer.passedS(this.delay.getValue().intValue())) break;
                if (this.packetMessage.getValue().booleanValue()) {
                    AutoBully.mc.player.connection.sendPacket(new CPacketChatMessage("/msg " + this.player.getValue() + " <AutoBully> Hola perro hijueputa, cuando aprendes pvp? tonto culiao"));
                    this.timer.reset();
                    break;
                }
                AutoBully.mc.player.sendChatMessage("/msg " + this.player.getValue() + " <AutoBully> Hola perro hijueputa, cuando aprendes pvp? tonto culiao");
                this.timer.reset();
            }
        }
    }

    @Override
    public void onDisable() {
        this.notAnnounced = true;
    }

    public static enum Amount {
        Once,
        Continual;

    }

    public static enum Message {
        HelloFan,
        WhenDidYouLearnPvP;

    }
}
