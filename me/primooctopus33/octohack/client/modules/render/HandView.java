package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class HandView
extends Module {
    private static HandView INSTANCE = new HandView();
    public Setting<Boolean> normalOffset = this.register(new Setting<Boolean>("OffNormal", false));
    public Setting<Integer> viewAlpha;
    public Setting<Float> offset = this.register(new Setting<Object>("Offset", Float.valueOf(0.7f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.normalOffset.getValue()));
    public Setting<Float> offX = this.register(new Setting<Object>("OffX", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), v -> this.normalOffset.getValue() == false));
    public Setting<Float> offY = this.register(new Setting<Object>("OffY", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), v -> this.normalOffset.getValue() == false));
    public Setting<Float> mainX = this.register(new Setting<Float>("MainX", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    public Setting<Float> mainY = this.register(new Setting<Float>("MainY", Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    public Setting<Float> sizeX;
    public Setting<Float> sizeY;
    public Setting<Float> sizeZ;
    public Setting<Float> rotationX;
    public Setting<Float> rotationY;
    public Setting<Float> rotationZ;
    public Setting<Float> positionX;
    public Setting<Float> positionY;
    public Setting<Float> positionZ;
    public Setting<Float> itemFOV;

    public HandView() {
        super("HandView", "Allows you to customize how items look in your hand", Module.Category.RENDER, false, false, false);
        this.viewAlpha = this.register(new Setting<Integer>("Item Alpha", 200, 0, 255));
        this.sizeX = this.register(new Setting<Float>("SizeX", Float.valueOf(1.8f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
        this.sizeY = this.register(new Setting<Float>("SizeY", Float.valueOf(0.6f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
        this.sizeZ = this.register(new Setting<Float>("SizeZ", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
        this.rotationX = this.register(new Setting<Float>("RotationX", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
        this.rotationY = this.register(new Setting<Float>("RotationY", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
        this.rotationZ = this.register(new Setting<Float>("RotationZ", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
        this.positionX = this.register(new Setting<Float>("PositionX", Float.valueOf(0.1f), Float.valueOf(-2.0f), Float.valueOf(2.0f)));
        this.positionY = this.register(new Setting<Float>("PositionY", Float.valueOf(0.3f), Float.valueOf(-2.0f), Float.valueOf(2.0f)));
        this.positionZ = this.register(new Setting<Float>("PositionZ", Float.valueOf(0.1f), Float.valueOf(-2.0f), Float.valueOf(2.0f)));
        this.itemFOV = this.register(new Setting<Float>("ItemFOV", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f)));
        this.setInstance();
    }

    public static HandView getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new HandView();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.normalOffset.getValue().booleanValue()) {
            HandView.mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offset.getValue().floatValue();
        }
    }
}
