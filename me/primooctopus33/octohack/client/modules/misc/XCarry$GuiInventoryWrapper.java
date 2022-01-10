package me.primooctopus33.octohack.client.modules.misc;

import java.io.IOException;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

class XCarry$GuiInventoryWrapper
extends GuiInventory {
    XCarry$GuiInventoryWrapper() {
        super((EntityPlayer)Util.mc.player);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (XCarry.this.isEnabled() && (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))) {
            XCarry.this.guiNeedsClose.set(true);
            this.mc.displayGuiScreen(null);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void onGuiClosed() {
        if (XCarry.this.guiCloseGuard || !XCarry.this.isEnabled()) {
            super.onGuiClosed();
        }
    }
}
