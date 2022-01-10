package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class NoEntityTrace
extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace();
    public Setting<Boolean> pickaxe = this.register(new Setting<Boolean>("Pickaxe", true));
    public Setting<Boolean> crystal = this.register(new Setting<Boolean>("Crystal", true));
    public Setting<Boolean> gapple = this.register(new Setting<Boolean>("Gapple", true));

    public NoEntityTrace() {
        super("NoEntityTrace", "Allows you to interact through entities as if they were not there", Module.Category.PLAYER, false, false, false);
        this.setInstance();
    }

    public static NoEntityTrace getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new NoEntityTrace();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
