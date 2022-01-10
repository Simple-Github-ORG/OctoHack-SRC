package me.primooctopus33.octohack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil
implements Util {
    public static short actionNumber = 0;
    private static int currentItem;

    public static int getSlot() {
        return InventoryUtil.mc.player.inventory.currentItem;
    }

    public static void setSlot(int slot) {
        if (slot > 8 || slot < 0) {
            return;
        }
        InventoryUtil.mc.player.inventory.currentItem = slot;
    }

    public static int getPlaceableItem() {
        ArrayList<Object> item = new ArrayList<Object>();
        for (int i1 = 0; i1 < 9; ++i1) {
            if (!(((ItemStack)InventoryUtil.mc.player.inventory.mainInventory.get(i1)).getItem() instanceof ItemBlock)) continue;
            item.add(InventoryUtil.mc.player.inventory.mainInventory.get(i1));
        }
        item.sort((a, b) -> b.getCount() - a.getCount());
        if (item.size() >= 1) {
            return InventoryUtil.mc.player.inventory.mainInventory.indexOf(item.get(0));
        }
        return -1;
    }

    public static int pickItem(int item) {
        ArrayList<Object> filter = new ArrayList<Object>();
        for (int i1 = 0; i1 < 9; ++i1) {
            if (Item.getIdFromItem((Item)((ItemStack)InventoryUtil.mc.player.inventory.mainInventory.get(i1)).getItem()) != item) continue;
            filter.add(InventoryUtil.mc.player.inventory.mainInventory.get(i1));
        }
        if (filter.size() >= 1) {
            return InventoryUtil.mc.player.inventory.mainInventory.indexOf(filter.get(0));
        }
        return -1;
    }

    public static int pickItem(int item, boolean allowInventory) {
        ArrayList<Object> filter = new ArrayList<Object>();
        for (int i1 = 0; i1 < (allowInventory ? InventoryUtil.mc.player.inventory.mainInventory.size() : 9); ++i1) {
            if (Item.getIdFromItem((Item)((ItemStack)InventoryUtil.mc.player.inventory.mainInventory.get(i1)).getItem()) != item) continue;
            filter.add(InventoryUtil.mc.player.inventory.mainInventory.get(i1));
        }
        if (filter.size() >= 1) {
            return InventoryUtil.mc.player.inventory.mainInventory.indexOf(filter.get(0));
        }
        return -1;
    }

    public static int findFirst(Class<? extends Item> clazz) {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (!InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem().getClass().equals(clazz)) continue;
            b = a;
            break;
        }
        return b;
    }

    public static void switchToSlotGhost(int slot) {
        InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }

    public static int getHotbarItemSlot(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (!InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int find(Class<? extends Item> clazz) {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (!InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem().getClass().equals(clazz)) continue;
            b = a;
        }
        return b;
    }

    public static int findFirst(Item item) {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem() != item) continue;
            b = a;
            break;
        }
        return b;
    }

    public static boolean switchTo(Item item) {
        int a = InventoryUtil.find(item);
        if (a == -1) {
            return false;
        }
        InventoryUtil.mc.player.inventory.currentItem = a;
        InventoryUtil.mc.playerController.updateController();
        return true;
    }

    public static int getItemSlot(Item items) {
        for (int i = 0; i < 36; ++i) {
            Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if (item != items) continue;
            if (i < 9) {
                i += 36;
            }
            return i;
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        if (slot != -1 && InventoryUtil.mc.player.inventory.currentItem != slot) {
            InventoryUtil.mc.player.inventory.currentItem = slot;
            InventoryUtil.mc.playerController.updateController();
        }
    }

    public static int amountInHotbar(Item item) {
        return InventoryUtil.amountInHotbar(item, true);
    }

    public static int amountInHotbar(Item item, boolean offhand) {
        int quantity = 0;
        for (int i = 44; i > 35; --i) {
            ItemStack stackInSlot = InventoryUtil.mc.player.inventoryContainer.getSlot(i).getStack();
            if (stackInSlot.getItem() != item) continue;
            quantity += stackInSlot.getCount();
        }
        if (InventoryUtil.mc.player.getHeldItemOffhand().getItem() == item && offhand) {
            quantity += InventoryUtil.mc.player.getHeldItemOffhand().getCount();
        }
        return quantity;
    }

    public static int amountBlockInHotbar(Block block) {
        return InventoryUtil.amountInHotbar(new ItemStack(block).getItem());
    }

    public static int amountBlockInHotbar(Block block, boolean offhand) {
        return InventoryUtil.amountInHotbar(new ItemStack(block).getItem(), offhand);
    }

    public static void moveItem(int before, int after) {
        InventoryUtil.mc.playerController.windowClick(InventoryUtil.mc.player.inventoryContainer.windowId, before, 0, ClickType.PICKUP, InventoryUtil.mc.player);
        InventoryUtil.mc.playerController.windowClick(InventoryUtil.mc.player.inventoryContainer.windowId, after, 0, ClickType.PICKUP, InventoryUtil.mc.player);
        InventoryUtil.mc.playerController.windowClick(InventoryUtil.mc.player.inventoryContainer.windowId, before, 0, ClickType.PICKUP, InventoryUtil.mc.player);
    }

    public static void clickWindow(int slotIn, ClickType type) {
    }

    public static void push() {
        currentItem = InventoryUtil.mc.player.inventory.currentItem;
    }

    public static void pop() {
        InventoryUtil.mc.player.inventory.currentItem = currentItem;
    }

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (InventoryUtil.mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.playerController.updateController();
        } else {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.player.inventory.currentItem = slot;
            InventoryUtil.mc.playerController.updateController();
        }
    }

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = InventoryUtil.findHotbarBlock(clazz);
        if (slot > -1) {
            InventoryUtil.switchToHotbarSlot(slot, silent);
        }
    }

    public static List<Integer> getItemInventory(Item item) {
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (int i = 9; i < 36; ++i) {
            Item target = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (!(item instanceof ItemBlock) || !((ItemBlock)((Object)item)).getBlock().equals(item)) continue;
            ints.add(i);
        }
        if (ints.size() == 0) {
            ints.add(-1);
        }
        return ints;
    }

    public static int findInventoryWool(boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (!(entry.getValue().getItem() instanceof ItemBlock)) continue;
            ItemBlock wool = (ItemBlock)((Object)entry.getValue().getItem());
            if (wool.getBlock().blockMaterial != Material.CLOTH || entry.getKey() == 45 && !offHand) continue;
            slot.set(entry.getKey());
            return slot.get();
        }
        return slot.get();
    }

    public static boolean isNull(ItemStack stack) {
        return stack == null || stack.getItem() instanceof ItemAir;
    }

    public static void switchToSlot(Block block) {
        if (InventoryUtil.getBlockInHotbar(block) != -1 && InventoryUtil.mc.player.inventory.currentItem != InventoryUtil.getBlockInHotbar(block)) {
            InventoryUtil.mc.player.inventory.currentItem = InventoryUtil.getBlockInHotbar(block);
        }
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock)((Object)stack.getItem())).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock)((Object)stack.getItem())).getBlock()) != blockIn) continue;
            return i;
        }
        return -1;
    }

    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem((Item)item) != Item.getIdFromItem((Item)input)) continue;
            return i;
        }
        return -1;
    }

    public static int findStackInventory(Item input) {
        return InventoryUtil.findStackInventory(input, false);
    }

    public static int findStackInventory(Item input, boolean withHotbar) {
        int i;
        int n = i;
        for (i = withHotbar ? 0 : 9; i < 36; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem((Item)input) != Item.getIdFromItem((Item)item)) continue;
            return i + (i < 9 ? 36 : 0);
        }
        return -1;
    }

    public static int findItemInventorySlot(Item item, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() != item || entry.getKey() == 45 && !offHand) continue;
            slot.set(entry.getKey());
            return slot.get();
        }
        return slot.get();
    }

    public static List<Integer> findEmptySlots(boolean withXCarry) {
        ArrayList<Integer> outPut = new ArrayList<Integer>();
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (!entry.getValue().isEmpty && entry.getValue().getItem() != Items.AIR) continue;
            outPut.add(entry.getKey());
        }
        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
                outPut.add(i);
            }
        }
        return outPut;
    }

    public static int findInventoryBlock(Class clazz, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (!InventoryUtil.isBlock(entry.getValue().getItem(), clazz) || entry.getKey() == 45 && !offHand) continue;
            slot.set(entry.getKey());
            return slot.get();
        }
        return slot.get();
    }

    public static boolean isBlock(Item item, Class clazz) {
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock)((Object)item)).getBlock();
            return clazz.isInstance(block);
        }
        return false;
    }

    public static void confirmSlot(int slot) {
        InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        InventoryUtil.mc.player.inventory.currentItem = slot;
        InventoryUtil.mc.playerController.updateController();
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return InventoryUtil.getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, (ItemStack)InventoryUtil.mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    public static boolean[] switchItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Class clazz) {
        boolean[] switchedItemSwitched = new boolean[]{switchedItem, false};
        switch (mode) {
            case NORMAL: {
                if (!back && !switchedItem) {
                    InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(clazz), false);
                    switchedItemSwitched[0] = true;
                } else if (back && switchedItem) {
                    InventoryUtil.switchToHotbarSlot(lastHotbarSlot, false);
                    switchedItemSwitched[0] = false;
                }
                switchedItemSwitched[1] = true;
                break;
            }
            case SILENT: {
                if (!back && !switchedItem) {
                    InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(clazz), true);
                    switchedItemSwitched[0] = true;
                } else if (back && switchedItem) {
                    switchedItemSwitched[0] = false;
                    OctoHack.inventoryManager.recoverSilent(lastHotbarSlot);
                }
                switchedItemSwitched[1] = true;
                break;
            }
            case NONE: {
                switchedItemSwitched[1] = back || InventoryUtil.mc.player.inventory.currentItem == InventoryUtil.findHotbarBlock(clazz);
            }
        }
        return switchedItemSwitched;
    }

    public static boolean[] switchItemToItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Item item) {
        boolean[] switchedItemSwitched = new boolean[]{switchedItem, false};
        switch (mode) {
            case NORMAL: {
                if (!back && !switchedItem) {
                    InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(item), false);
                    switchedItemSwitched[0] = true;
                } else if (back && switchedItem) {
                    InventoryUtil.switchToHotbarSlot(lastHotbarSlot, false);
                    switchedItemSwitched[0] = false;
                }
                switchedItemSwitched[1] = true;
                break;
            }
            case SILENT: {
                if (!back && !switchedItem) {
                    InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(item), true);
                    switchedItemSwitched[0] = true;
                } else if (back && switchedItem) {
                    switchedItemSwitched[0] = false;
                    OctoHack.inventoryManager.recoverSilent(lastHotbarSlot);
                }
                switchedItemSwitched[1] = true;
                break;
            }
            case NONE: {
                switchedItemSwitched[1] = back || InventoryUtil.mc.player.inventory.currentItem == InventoryUtil.getItemHotbar(item);
            }
        }
        return switchedItemSwitched;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = InventoryUtil.mc.player.getHeldItemMainhand();
        result = InventoryUtil.isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = InventoryUtil.mc.player.getHeldItemOffhand();
            result = InventoryUtil.isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance(item)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem((Item)item);
            return clazz.isInstance(block);
        }
        return false;
    }

    public static int getBlockInHotbar(Block block) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (!(item instanceof ItemBlock) || !((ItemBlock)((Object)item)).getBlock().equals(block)) continue;
            return i;
        }
        return -1;
    }

    public static int getEmptyXCarry() {
        for (int i = 1; i < 5; ++i) {
            Slot craftingSlot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
            ItemStack craftingStack = craftingSlot.getStack();
            if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
            return i;
        }
        return -1;
    }

    public static boolean isSlotEmpty(int i) {
        Slot slot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
        ItemStack stack = slot.getStack();
        return stack.isEmpty();
    }

    public static int convertHotbarToInv(int input) {
        return 36 + input;
    }

    public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
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
        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }
        return stack1.getItemDamage() == stack2.getItemDamage();
    }

    public static EntityEquipmentSlot getEquipmentFromSlot(int slot) {
        if (slot == 5) {
            return EntityEquipmentSlot.HEAD;
        }
        if (slot == 6) {
            return EntityEquipmentSlot.CHEST;
        }
        if (slot == 7) {
            return EntityEquipmentSlot.LEGS;
        }
        return EntityEquipmentSlot.FEET;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            ItemArmor armor;
            ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() == Items.AIR || !(s.getItem() instanceof ItemArmor) || (armor = (ItemArmor)s.getItem()).getEquipmentSlot() != type) continue;
            float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)s);
            boolean cursed = binding && EnchantmentHelper.hasBindingCurse((ItemStack)s);
            boolean bl = cursed;
            if (!(currentDamage > damage) || cursed) continue;
            damage = currentDamage;
            slot = i;
        }
        return slot;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = InventoryUtil.findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0f;
            for (int i = 1; i < 5; ++i) {
                ItemArmor armor;
                Slot craftingSlot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || !(craftingStack.getItem() instanceof ItemArmor) || (armor = (ItemArmor)craftingStack.getItem()).getEquipmentSlot() != type) continue;
                float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)craftingStack);
                boolean cursed = binding && EnchantmentHelper.hasBindingCurse((ItemStack)craftingStack);
                boolean bl = cursed;
                if (!(currentDamage > damage) || cursed) continue;
                damage = currentDamage;
                slot = i;
            }
        }
        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
        int slot = InventoryUtil.findItemInventorySlot(item, offHand);
        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Item craftingStackItem;
                Slot craftingSlot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || (craftingStackItem = craftingStack.getItem()) != item) continue;
                slot = i;
            }
        }
        return slot;
    }

    public static int findBlockSlotInventory(Class clazz, boolean offHand, boolean withXCarry) {
        int slot = InventoryUtil.findInventoryBlock(clazz, offHand);
        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Block block;
                Slot craftingSlot = (Slot)InventoryUtil.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR) continue;
                Item craftingStackItem = craftingStack.getItem();
                if (clazz.isInstance(craftingStackItem)) {
                    slot = i;
                    continue;
                }
                if (!(craftingStackItem instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock)((Object)craftingStackItem)).getBlock())) continue;
                slot = i;
            }
        }
        return slot;
    }

    public static int find(Item item) {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem() != item) continue;
            b = a;
        }
        return b;
    }

    public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
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

    public static enum Switch {
        NORMAL,
        SILENT,
        NONE;

    }
}
