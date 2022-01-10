package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class ReverseStep
extends Module {
    public final Setting<Double> fallSpeed = this.register(new Setting<Double>("Fall Speed", 2.0, 0.0, 10.0));
    public final Setting<Boolean> noLiquids = this.register(new Setting<Boolean>("No Liquids", true));

    public ReverseStep() {
        super("ReverseStep", "Makes you step down blocks faster", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.fullNullCheck()) {
            return;
        }
        if (this.noLiquids.getValue().booleanValue() && ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isInWater()) {
            return;
        }
        if (ReverseStep.mc.player.onGround) {
            ReverseStep.mc.player.motionY -= this.fallSpeed.getValue().doubleValue();
        }
    }
}
