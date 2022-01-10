package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class Nuker
extends Module {
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Creative));
    public final Setting<Integer> nukerRange = this.register(new Setting<Integer>("Nuker Range", 5, 0, 10));

    public Nuker() {
        super("Nuker", "Automatically breaks blocks around you", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.Creative) {
            // empty if block
        }
    }

    public static enum Mode {
        Creative;

    }
}
