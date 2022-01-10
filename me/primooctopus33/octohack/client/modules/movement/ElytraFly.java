package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFly
extends Module {
    private double hoverTarget;
    private boolean hoverState;
    public final Setting<Boolean> hover = this.register(new Setting<Boolean>("Hover", true));
    public final Setting<Boolean> autoOpen = this.register(new Setting<Boolean>("Auto Open", true));
    public final Setting<Double> speed = this.register(new Setting<Double>("Speed", 1.8, 0.0, 10.0));
    public final Setting<Double> downSpeed = this.register(new Setting<Double>("Down Speed", 2.0, 0.0, 10.0));
    public final Setting<Double> sinkSpeed = this.register(new Setting<Double>("Sink Speed", 0.1, 0.0, 10.0));
    public static boolean flyUp = false;
    public static boolean autoFly = false;
    public float packetYaw;

    public ElytraFly() {
        super("ElytraFly", "Makes it easier for you to fly when using an elytra", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        Packet packet;
        if (ElytraFly.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            if (!ElytraFly.mc.player.isElytraFlying()) {
                return;
            }
            packet = (CPacketPlayer)event.getPacket();
            packet.pitch = 0.0f;
            packet.yaw = this.packetYaw;
        }
        if (event.getPacket() instanceof CPacketEntityAction && (packet = (CPacketEntityAction)event.getPacket()).getAction() == CPacketEntityAction.Action.START_FALL_FLYING) {
            this.hoverTarget = ElytraFly.mc.player.posY + 0.35;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (ElytraFly.mc.player == null || !ElytraFly.mc.player.isElytraFlying()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            packet.pitch = ElytraFly.mc.player.rotationPitch;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        boolean doHover;
        if (ElytraFly.nullCheck()) {
            return;
        }
        if (!ElytraFly.mc.player.isElytraFlying()) {
            if (this.autoOpen.getValue().booleanValue() && !ElytraFly.mc.player.onGround && ElytraFly.mc.player.motionY < -0.04) {
                CPacketEntityAction packet = new CPacketEntityAction(ElytraFly.mc.player, CPacketEntityAction.Action.START_FALL_FLYING);
                ElytraFly.mc.player.connection.sendPacket(packet);
                ElytraFly.mc.timer.tickLength = 200.0f;
                event.setCanceled(true);
                return;
            }
            return;
        }
        ElytraFly.mc.timer.tickLength = 50.0f;
        if (this.hoverTarget < 0.0) {
            this.hoverTarget = ElytraFly.mc.player.posY;
        }
        boolean moveForward = ElytraFly.mc.gameSettings.keyBindForward.isKeyDown();
        boolean moveBackward = ElytraFly.mc.gameSettings.keyBindBack.isKeyDown();
        boolean moveLeft = ElytraFly.mc.gameSettings.keyBindLeft.isKeyDown();
        boolean moveRight = ElytraFly.mc.gameSettings.keyBindRight.isKeyDown();
        boolean moveUp = ElytraFly.mc.gameSettings.keyBindJump.isKeyDown();
        boolean moveDown = ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown();
        float moveForwardFactor = moveForward ? 1.0f : -1.0f;
        float yawDeg = ElytraFly.mc.player.rotationYaw;
        if (moveLeft && (moveForward || moveBackward)) {
            yawDeg -= 40.0f * moveForwardFactor;
        } else if (moveRight && (moveForward || moveBackward)) {
            yawDeg += 40.0f * moveForwardFactor;
        } else if (moveLeft) {
            yawDeg -= 90.0f;
        } else if (moveRight) {
            yawDeg += 90.0f;
        }
        if (moveBackward) {
            yawDeg -= 180.0f;
        }
        this.packetYaw = yawDeg;
        float yaw = (float)Math.toRadians(yawDeg);
        float pitch = (float)Math.toRadians(ElytraFly.mc.player.rotationPitch);
        double d8 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
        this.hoverState = this.hoverState ? ElytraFly.mc.player.posY < this.hoverTarget + 0.1 : ElytraFly.mc.player.posY < this.hoverTarget + 0.0;
        boolean bl = doHover = this.hoverState && this.hover.getValue() != false;
        if (moveUp || moveForward || moveBackward || moveLeft || moveRight || autoFly || OctoHack.moduleManager.isModuleEnabled("AutoWalk")) {
            if ((moveUp || doHover || flyUp) && d8 > 1.0) {
                if (ElytraFly.mc.player.motionX == 0.0 && ElytraFly.mc.player.motionZ == 0.0) {
                    ElytraFly.mc.player.motionY = this.downSpeed.getValue();
                } else {
                    double d6 = 1.0;
                    double d10 = d8 * 0.2 * 0.04;
                    ElytraFly.mc.player.motionY += d10 * 3.2;
                    ElytraFly.mc.player.motionX -= (double)(-MathHelper.sin((float)yaw)) * d10 / d6;
                    ElytraFly.mc.player.motionZ -= (double)MathHelper.cos((float)yaw) * d10 / d6;
                    if (d6 > 0.0) {
                        ElytraFly.mc.player.motionX += ((double)(-MathHelper.sin((float)yaw)) / d6 * d8 - ElytraFly.mc.player.motionX) * 0.3;
                        ElytraFly.mc.player.motionZ += ((double)MathHelper.cos((float)yaw) / d6 * d8 - ElytraFly.mc.player.motionZ) * 0.3;
                    }
                    ElytraFly.mc.player.motionX *= (double)0.99f;
                    ElytraFly.mc.player.motionY *= (double)0.98f;
                    ElytraFly.mc.player.motionZ *= (double)0.99f;
                }
            } else {
                ElytraFly.mc.player.motionX = (double)(-MathHelper.sin((float)yaw)) * this.speed.getValue();
                ElytraFly.mc.player.motionY = -this.sinkSpeed.getValue().doubleValue();
                ElytraFly.mc.player.motionZ = (double)MathHelper.cos((float)yaw) * this.speed.getValue();
            }
        } else {
            ElytraFly.mc.player.motionX = 0.0;
            ElytraFly.mc.player.motionY = 0.0;
            ElytraFly.mc.player.motionZ = 0.0;
        }
        if (moveDown) {
            ElytraFly.mc.player.motionY = -this.downSpeed.getValue().doubleValue();
        }
        if (moveUp || moveDown) {
            this.hoverTarget = ElytraFly.mc.player.posY;
        }
        event.setCanceled(true);
    }
}
