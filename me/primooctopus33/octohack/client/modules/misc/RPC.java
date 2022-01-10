package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.DiscordPresence;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class RPC
extends Module {
    public static RPC INSTANCE;
    public Setting<Boolean> showIP = this.register(new Setting<Boolean>("ShowIP", Boolean.valueOf(true), "Shows the server IP in your discord presence."));
    public Setting<String> state = this.register(new Setting<String>("State", "OctoHack 0.1.7", "Sets the state of the DiscordRPC."));
    public Setting<String> largeImageText = this.register(new Setting<String>("LargeImageText", "OctoHack", "Sets the large image text of the DiscordRPC."));
    public Setting<String> smallImageText = this.register(new Setting<String>("SmallImageText", "h", "Sets the small image text of the DiscordRPC."));
    public Setting<LargeImage> largeImage = this.register(new Setting<LargeImage>("LargeImage", LargeImage.Octohack));
    public Setting<SmallImage> smallImage = this.register(new Setting<SmallImage>("SmallImage", SmallImage.Octohack));
    public LargeImage lastLargeImage;
    public SmallImage lastSmallImage;

    public RPC() {
        super("RPC", "Discord rich presence", Module.Category.MISC, false, false, false);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    @Override
    public void onUpdate() {
        if (this.lastLargeImage != this.largeImage.getValue() || this.lastSmallImage != this.smallImage.getValue()) {
            DiscordPresence.stop();
            DiscordPresence.start();
        }
        this.lastLargeImage = this.largeImage.getValue();
        this.lastSmallImage = this.smallImage.getValue();
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }

    public static enum SmallImage {
        Octohack;

    }

    public static enum LargeImage {
        Octohack;

    }
}
