package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;

public class RenderItemEvent
extends EventStage {
    double mainX;
    double mainY;
    double mainZ;
    double offX;
    double offY;
    double offZ;
    double mainYaw;
    double mainPitch;
    double mainR;
    double offYaw;
    double offPitch;
    double offR;
    double mainHandScaleX;
    double mainHandScaleY;
    double mainHandScaleZ;
    double offHandScaleX;
    double offHandScaleY;
    double offHandScaleZ;

    public RenderItemEvent(double mainX, double mainY, double mainZ, double offX, double offY, double offZ, double mainYaw, double mainPitch, double mainR, double offYaw, double offPitch, double offR, double mainHandScaleX, double mainHandScaleY, double mainHandScaleZ, double offHandScaleX, double offHandScaleY, double offHandScaleZ) {
        this.mainX = mainX;
        this.mainY = mainY;
        this.mainZ = mainZ;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.mainYaw = mainYaw;
        this.mainPitch = mainPitch;
        this.mainR = mainR;
        this.offYaw = offYaw;
        this.offPitch = offPitch;
        this.offR = offR;
        this.mainHandScaleX = mainHandScaleX;
        this.mainHandScaleY = mainHandScaleY;
        this.mainHandScaleZ = mainHandScaleZ;
        this.offHandScaleX = offHandScaleX;
        this.offHandScaleY = offHandScaleY;
        this.offHandScaleZ = offHandScaleZ;
    }

    public void setMainX(double v) {
        this.mainX = v;
    }

    public void setMainY(double v) {
        this.mainY = v;
    }

    public void setMainZ(double v) {
        this.mainZ = v;
    }

    public void setOffX(double v) {
        this.offX = v;
    }

    public void setOffY(double v) {
        this.offY = v;
    }

    public void setOffZ(double v) {
        this.offZ = v;
    }

    public void setOffYaw(double v) {
        this.offYaw = v;
    }

    public void setOffPitch(double v) {
        this.offPitch = v;
    }

    public void setOffR(double v) {
        this.offR = v;
    }

    public void setMainYaw(double v) {
        this.mainYaw = v;
    }

    public void setMainPitch(double v) {
        this.mainPitch = v;
    }

    public void setMainR(double v) {
        this.mainR = v;
    }

    public void setMainHandScaleX(double v) {
        this.mainHandScaleX = v;
    }

    public void setMainHandScaleY(double v) {
        this.mainHandScaleY = v;
    }

    public void setMainHandScaleZ(double v) {
        this.mainHandScaleZ = v;
    }

    public void setOffHandScaleX(double v) {
        this.offHandScaleX = v;
    }

    public void setOffHandScaleY(double v) {
        this.offHandScaleY = v;
    }

    public void setOffHandScaleZ(double v) {
        this.offHandScaleZ = v;
    }

    public double getMainX() {
        return this.mainX;
    }

    public double getMainY() {
        return this.mainY;
    }

    public double getMainZ() {
        return this.mainZ;
    }

    public double getOffX() {
        return this.offX;
    }

    public double getOffY() {
        return this.offY;
    }

    public double getOffZ() {
        return this.offZ;
    }

    public double getMainYaw() {
        return this.mainYaw;
    }

    public double getMainPitch() {
        return this.mainPitch;
    }

    public double getMainR() {
        return this.mainR;
    }

    public double getOffYaw() {
        return this.offYaw;
    }

    public double getOffPitch() {
        return this.offPitch;
    }

    public double getOffR() {
        return this.offR;
    }

    public double getMainHandScaleX() {
        return this.mainHandScaleX;
    }

    public double getMainHandScaleY() {
        return this.mainHandScaleY;
    }

    public double getMainHandScaleZ() {
        return this.mainHandScaleZ;
    }

    public double getOffHandScaleX() {
        return this.offHandScaleX;
    }

    public double getOffHandScaleY() {
        return this.offHandScaleY;
    }

    public double getOffHandScaleZ() {
        return this.offHandScaleZ;
    }
}
