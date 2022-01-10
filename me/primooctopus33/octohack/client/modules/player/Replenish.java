package me.primooctopus33.octohack.client.modules.player;

import java.util.HashMap;
import java.util.Map;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.PairUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class Replenish
extends Module {
    public final Setting<Integer> threshold = this.register(new Setting<Integer>("Threshold", 2, 1, 63));
    public final Setting<Integer> tickDelay = this.register(new Setting<Integer>("Tick Delay", 2, 0, 10));
    private int delayStep = 0;

    public Replenish() {
        super("Replenish", "Automatically refills items in your hotbar", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (Replenish.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        PairUtil<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        int inventorySlot = slots.getKey();
        int hotbarSlot = slots.getValue();
        Replenish.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, Replenish.mc.player);
        Replenish.mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, Replenish.mc.player);
        Replenish.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, Replenish.mc.player);
        Replenish.mc.playerController.updateController();
    }

    private PairUtil<Integer, Integer> findReplenishableHotbarSlot() {
        PairUtil<Integer, Integer> returnPair = null;
        for (Map.Entry<Integer, ItemStack> hotbarSlot : this.getHotbar().entrySet()) {
            int inventorySlot;
            ItemStack stack = hotbarSlot.getValue();
            if (stack.isEmpty || stack.getItem() == Items.AIR || !stack.isStackable() || stack.stackSize >= stack.getMaxStackSize() || stack.stackSize > this.threshold.getValue() || (inventorySlot = this.findCompatibleInventorySlot(stack)) == -1) continue;
            returnPair = new PairUtil<Integer, Integer>(inventorySlot, hotbarSlot.getKey());
        }
        return returnPair;
    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {
        int inventorySlot = -1;
        int smallestStackSize = 999;
        for (Map.Entry<Integer, ItemStack> entry : this.getInventory().entrySet()) {
            int currentStackSize;
            ItemStack inventoryStack = entry.getValue();
            if (inventoryStack.isEmpty || inventoryStack.getItem() == Items.AIR || !this.isCompatibleStacks(hotbarStack, inventoryStack) || smallestStackSize <= (currentStackSize = ((ItemStack)Replenish.mc.player.inventoryContainer.getInventory().get((int)entry.getKey().intValue())).stackSize)) continue;
            smallestStackSize = currentStackSize;
            inventorySlot = entry.getKey();
        }
        return inventorySlot;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            Block block1 = ((ItemBlock)((Object)stack1.getItem())).getBlock();
            Block block2 = ((ItemBlock)((Object)stack2.getItem())).getBlock();
            if (!block1.blockMaterial.equals(block2.blockMaterial)) {
                return false;
            }
        }
        return stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItemDamage() == stack2.getItemDamage();
    }

    private Map<Integer, ItemStack> getInventory() {
        return this.getInvSlots(9, 35);
    }

    private Map<Integer, ItemStack> getHotbar() {
        return this.getInvSlots(36, 44);
    }

    private Map<Integer, ItemStack> getInvSlots(int current, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)Replenish.mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }
}
