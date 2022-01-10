package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class AntiAim
extends Module {
    public final Setting<Double> speed = this.register(new Setting<Double>("Spin Speed", 5.0, -20.0, 20.0));
    public final Setting<Double> jitterOffset = this.register(new Setting<Double>("Jitter Offset", 230.0, -360.0, 360.0));
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Spin));
    public int ticks;

    public AntiAim() {
        super("AntiAim", "Rotates your player to make you harder to aim at", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        this.ticks = 0;
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Mode.Spin) {
            float f = this.speed.getValue().floatValue();
        }
    }

    public static enum Mode {
        Spin;

    }
}
