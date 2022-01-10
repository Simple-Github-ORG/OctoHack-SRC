package me.primooctopus33.octohack.manager;

import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotationManager
extends Feature {
    public float yaw;
    public float pitch;

    public void updateRotations() {
        this.yaw = RotationManager.mc.player.rotationYaw;
        this.pitch = RotationManager.mc.player.rotationPitch;
    }

    public void restoreRotations() {
        RotationManager.mc.player.rotationYaw = this.yaw;
        RotationManager.mc.player.rotationYawHead = this.yaw;
        RotationManager.mc.player.rotationPitch = this.pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public void setPlayerYaw(float yaw) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), entity.getPositionEyes(Util.mc.getRenderPartialTicks()));
        RotationManager.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], RotationManager.mc.player.onGround));
    }

    public void setPlayerPitch(float pitch) {
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getDirection4D() {
        return RotationUtil.getDirection4D();
    }

    public String getDirection4D(boolean northRed) {
        return RotationUtil.getDirection4D(northRed);
    }
}
