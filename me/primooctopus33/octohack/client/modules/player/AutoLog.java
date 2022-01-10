package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog
extends Module {
    private static AutoLog INSTANCE = new AutoLog();
    private final Setting<Float> health = this.register(new Setting<Float>("Health", Float.valueOf(16.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    private final Setting<Boolean> bed = this.register(new Setting<Boolean>("Beds", true));
    private final Setting<Float> range = this.register(new Setting<Object>("BedRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.bed.getValue()));
    private final Setting<Boolean> logout = this.register(new Setting<Boolean>("LogoutOff", true));

    public AutoLog() {
        super("AutoLog", "Automatically logs out when you're in combat", Module.Category.PLAYER, false, false, false);
        this.setInstance();
    }

    public static AutoLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoLog();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (!AutoLog.nullCheck() && AutoLog.mc.player.getHealth() <= this.health.getValue().floatValue()) {
            OctoHack.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket(new SPacketDisconnect(new TextComponentString("Your health was below required health, Automatically logged out.")));
            if (this.logout.getValue().booleanValue()) {
                this.disable();
            }
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        SPacketBlockChange packet;
        if (event.getPacket() instanceof SPacketBlockChange && this.bed.getValue().booleanValue() && (packet = (SPacketBlockChange)event.getPacket()).getBlockState().getBlock() == Blocks.BED && AutoLog.mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(this.range.getValue().floatValue())) {
            OctoHack.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket(new SPacketDisconnect(new TextComponentString("There was a bed nearby, Automatically logged out.")));
            if (this.logout.getValue().booleanValue()) {
                this.disable();
            }
        }
    }
}
