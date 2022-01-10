package me.primooctopus33.octohack.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;

public class NickHider
extends Module {
    public final Setting<String> NameString = this.register(new Setting<String>("Name", "New Name Here..."));
    private static NickHider instance;

    public NickHider() {
        super("NameSpoofer", "Changes your Name Client Sided", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + this.NameString.getValue());
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }
}
