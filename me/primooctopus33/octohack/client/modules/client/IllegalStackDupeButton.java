package me.primooctopus33.octohack.client.modules.client;

import me.primooctopus33.octohack.client.modules.Module;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;

public class IllegalStackDupeButton
extends Module {
    public boolean validGui;

    public IllegalStackDupeButton() {
        super("IllegalStackDupeButton", "Shows a Dupe button while riding an Entity", Module.Category.CLIENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        this.validGui = IllegalStackDupeButton.mc.currentScreen instanceof GuiScreenHorseInventory;
    }
}
