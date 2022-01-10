package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AntiVoid
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Singleplayer));
    public Setting<Integer> singleplayerTPY = this.register(new Setting<Object>("Singleplayer TP Y", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(255), v -> this.mode.getValue() == Mode.Singleplayer));
    public Setting<Integer> singleplayerVoidY = this.register(new Setting<Object>("Singleplayer Void Y", Integer.valueOf(-15), Integer.valueOf(-255), Integer.valueOf(255), v -> this.mode.getValue() == Mode.Singleplayer));
    public Setting<Double> yLevel = this.register(new Setting<Double>("Normal Void Y", Double.valueOf(1.0), Double.valueOf(0.1), Double.valueOf(5.0), v -> this.mode.getValue() == Mode.Normal));
    public Setting<Double> yForce = this.register(new Setting<Double>("Y Move Force", Double.valueOf(0.1), Double.valueOf(0.0), Double.valueOf(1.0), v -> this.mode.getValue() == Mode.Normal));

    public AntiVoid() {
        super("AntiVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.Normal && !AntiVoid.mc.player.noClip && AntiVoid.mc.player.posY <= this.yLevel.getValue()) {
            RayTraceResult trace = AntiVoid.mc.world.rayTraceBlocks(AntiVoid.mc.player.getPositionVector(), new Vec3d(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ), false, false, false);
            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
                return;
            }
            AntiVoid.mc.player.motionY = this.yForce.getValue();
            if (AntiVoid.mc.player.getRidingEntity() != null) {
                AntiVoid.mc.player.getRidingEntity().motionY = this.yForce.getValue();
            }
        }
        if (this.mode.getValue() == Mode.Singleplayer && !AntiVoid.mc.player.noClip && AntiVoid.mc.player.posY <= (double)this.singleplayerVoidY.getValue().intValue()) {
            AntiVoid.mc.player.connection.sendPacket(new CPacketChatMessage("/tp @s ~ " + this.singleplayerTPY.getValue() + " ~"));
        }
    }

    public static enum Mode {
        Singleplayer,
        Normal;

    }
}
