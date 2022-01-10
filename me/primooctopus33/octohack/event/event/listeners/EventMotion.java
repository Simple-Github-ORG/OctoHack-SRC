package me.primooctopus33.octohack.event.event.listeners;

import me.primooctopus33.octohack.event.event.Event;
import net.minecraft.util.math.BlockPos;

public class EventMotion
extends Event<EventMotion> {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    private double lastX;
    private double lastY;
    private double lastZ;
    public float lastYaw;
    public float lastPitch;
    public boolean lastOnGround;

    public boolean isModded() {
        return this.lastX != this.x || this.lastY != this.y || this.lastZ != this.z || this.lastYaw != this.yaw || this.lastPitch != this.pitch || this.lastOnGround != this.onGround;
    }

    public EventMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.lastOnGround = onGround;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
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

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(BlockPos pos) {
        this.x = (double)pos.getX() + 0.5;
        this.y = pos.getY();
        this.z = (double)pos.getZ() + 0.5;
    }
}
