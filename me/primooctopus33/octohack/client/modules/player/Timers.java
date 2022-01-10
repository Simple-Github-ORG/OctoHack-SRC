package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.TestUtil;

public class Timers
extends Module {
    public final Setting<Boolean> tpsSync = this.register(new Setting<Boolean>("TpsSync", false));
    public final Setting<Float> multiplier = this.register(new Setting<Float>("Multiplier", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));

    public Timers() {
        super("Timer", "Allows you to change the client ticks per second", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        Timers.mc.timer.tickLength = 50.0f / this.getMultiplier();
    }

    @Override
    public void onDisable() {
        Timers.mc.timer.tickLength = 50.0f;
    }

    public float getMultiplier() {
        if (this.isEnabled()) {
            if (this.tpsSync.getValue().booleanValue()) {
                float f = TestUtil.getTickRate() / 20.0f * this.multiplier.getValue().floatValue();
                if (f < 0.1f) {
                    f = 0.1f;
                }
                return f;
            }
            return this.multiplier.getValue().floatValue();
        }
        return 1.0f;
    }
}
