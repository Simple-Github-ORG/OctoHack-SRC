package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.Util;
import net.minecraft.inventory.ClickType;

public class InventoryUtil$Task {
    private final int slot;
    private final boolean update;
    private final boolean quickClick;

    public InventoryUtil$Task() {
        this.update = true;
        this.slot = -1;
        this.quickClick = false;
    }

    public InventoryUtil$Task(int slot) {
        this.slot = slot;
        this.quickClick = false;
        this.update = false;
    }

    public InventoryUtil$Task(int slot, boolean quickClick) {
        this.slot = slot;
        this.quickClick = quickClick;
        this.update = false;
    }

    public void run() {
        if (this.update) {
            Util.mc.playerController.updateController();
        }
        if (this.slot != -1) {
            Util.mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.player);
        }
    }

    public boolean isSwitching() {
        return !this.update;
    }
}
