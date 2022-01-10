package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class Ambience
extends Module {
    public final Setting<Integer> r = this.register(new Setting<Integer>("Red", 30, 0, 255));
    public final Setting<Integer> g = this.register(new Setting<Integer>("Green", 30, 0, 255));
    public final Setting<Integer> b = this.register(new Setting<Integer>("Blue", 30, 0, 255));
    public final Setting<Integer> a = this.register(new Setting<Integer>("Alpha", 30, 0, 255));

    public Ambience() {
        super("Ambience", "Allows you to change the ambience of your world", Module.Category.RENDER, true, false, false);
    }
}
