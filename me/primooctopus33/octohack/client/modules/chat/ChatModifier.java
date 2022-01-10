package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifier
extends Module {
    public Setting<Suffix> suffix = this.register(new Setting<Suffix>("Suffix", Suffix.NONE, "Your Suffix."));
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("Clean Chat", Boolean.valueOf(false), "Cleans your chat"));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("Infinite", Boolean.valueOf(false), "Makes your chat infinite."));
    public Setting<Boolean> autoQMain = this.register(new Setting<Boolean>("Auto Q Main", Boolean.valueOf(false), "Spams AutoQMain"));
    public Setting<Boolean> qNotification = this.register(new Setting<Object>("Q Notification", Boolean.valueOf(false), v -> this.autoQMain.getValue()));
    public Setting<Integer> qDelay = this.register(new Setting<Object>("Q Delay", Integer.valueOf(9), Integer.valueOf(1), Integer.valueOf(90), v -> this.autoQMain.getValue()));
    private final Timer timer = new Timer();
    private static ChatModifier INSTANCE = new ChatModifier();

    public ChatModifier() {
        super("Chat", "Modifies your chat", Module.Category.CHAT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (this.autoQMain.getValue().booleanValue()) {
            if (!this.shouldSendMessage(ChatModifier.mc.player)) {
                return;
            }
            if (this.qNotification.getValue().booleanValue()) {
                Command.sendMessage("<AutoQueueMain> Sending message: /queue main");
            }
            ChatModifier.mc.player.sendChatMessage("/queue main");
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
            switch (this.suffix.getValue()) {
                case OCTOHACK: {
                    s = s + " \u23d0 \u039e\uff2f\u1d04\u1d1b\u0e4f\u0266\u039b\u1d04\u13e6\u039e";
                    break;
                }
                case HEPHAESTUS: {
                    s = s + " \u23d0 \u041d\u03b5\u13ae\u043d\u15e9\u03b5\u0455\u01ad\u03c5\u0455";
                    break;
                }
                case TROLL: {
                    s = s + " \u23d0 ee\u1d07\u1d07\u1d1c\u0280\u1d1bh\u1d073\u1d04\u1d0b \u026a.2.1 \u23d0 \u1d18\u1d07\u0280\u028f\u1203s \u1d18\u1d0f\u0299\u1d0f\ua731 \u23d0 \u1d0b\u1d00\u1d0d1 \ua730\u026a\u1d20\u1d0755 \u23d0 \u1d18\u029c\u1d0f\u0299\u1d07\ua731\u1d07 \u23d0 \u1d0d\u1d07\u0262\u028f\u0274 \u026a\ua731 \ua730\u1d00\u1d1b - 3\u1d20\u1d1b \u23d0 \uff25\uff25\uff25uropa \u23d0 (\u3063\u25d4\u25e1\u25d4)\u3063 \u2665\ufe0f \u23d0 SMP WITHOUT AUXOL! \u2665\ufe0f \u23d0 Oguzmad.inc";
                }
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        if (!this.timer.passedS(this.qDelay.getValue().intValue())) {
            return false;
        }
        return player.getPosition().equals(new Vec3i(0, 240, 0));
    }

    public static enum Suffix {
        NONE,
        OCTOHACK,
        HEPHAESTUS,
        TROLL;

    }
}
