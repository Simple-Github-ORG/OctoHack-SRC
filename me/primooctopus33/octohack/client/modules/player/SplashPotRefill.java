package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class SplashPotRefill
extends Module {
    public SplashPotRefill() {
        super("SplashPotRefill", "Automatically refills Splash potions in your hotbar", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (!SplashPotRefill.mc.player.inventory.getCurrentItem().getItem().equals(Items.SPLASH_POTION)) {
            for (int i = 0; i < 45; ++i) {
                ItemStack stacks = SplashPotRefill.mc.player.openContainer.getSlot(i).getStack();
                if (stacks == ItemStack.EMPTY) continue;
                ItemPotion itemMine = Items.SPLASH_POTION;
                if (!SplashPotRefill.mc.player.getHeldItemMainhand().isEmpty() || SplashPotRefill.mc.currentScreen instanceof GuiChest || stacks.getItem() != itemMine) continue;
                SplashPotRefill.mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, SplashPotRefill.mc.player);
                SplashPotRefill.mc.playerController.windowClick(0, 36, 1, ClickType.PICKUP, SplashPotRefill.mc.player);
            }
        }
    }
}
