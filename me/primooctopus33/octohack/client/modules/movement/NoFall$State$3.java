package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.movement.NoFall;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

final class NoFall$State$3
extends NoFall.State {
    @Override
    public NoFall.State onUpdate() {
        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, Util.mc.player);
        Util.mc.playerController.updateController();
        int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
        if (slot == -1) {
            Command.sendMessage("\u00a7cElytra not found after regain?");
            return WAIT_FOR_NEXT_REQUIP;
        }
        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, Util.mc.player);
        Util.mc.playerController.updateController();
        bypassTimer.reset();
        return RESET_TIME;
    }
}
