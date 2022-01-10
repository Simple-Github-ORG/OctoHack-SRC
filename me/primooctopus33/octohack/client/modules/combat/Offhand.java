package me.primooctopus33.octohack.client.modules.combat;

import java.lang.invoke.LambdaMetafactory;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.ToIntFunction;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.ProcessRightClickBlockEvent;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Offhand
extends Module {
    private static Offhand instance;
    private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
    private final Timer timer = new Timer();
    private final Timer secondTimer = new Timer();
    public Setting<Boolean> crystal = this.register(new Setting<Boolean>("OffhandCrystal", true));
    public Setting<Float> crystalHealth = this.register(new Setting<Float>("OffhandHP", Float.valueOf(13.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    public Setting<Float> crystalHoleHealth = this.register(new Setting<Float>("OffhandHoleHP", Float.valueOf(3.5f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    public Setting<Boolean> gapple = this.register(new Setting<Boolean>("RightClickGapple", true));
    public Setting<Boolean> crystalGap = this.register(new Setting<Boolean>("CrystalGapple", true));
    public Setting<Boolean> pickGap = this.register(new Setting<Boolean>("PickaxeGapple", true));
    public Setting<Boolean> swordCrouchPotion = this.register(new Setting<Boolean>("SwordCrouchPotion", true));
    public Setting<Boolean> pickCrouchChorus = this.register(new Setting<Boolean>("PickCrouchChorus", true));
    public Setting<Boolean> antiGappleFail = this.register(new Setting<Boolean>("AntiGapFail", false));
    public Setting<Boolean> armorCheck = this.register(new Setting<Boolean>("ArmorCheck", true));
    public Setting<Integer> actions = this.register(new Setting<Integer>("Packets", 4, 1, 4));
    public Setting<Boolean> fallDistance = this.register(new Setting<Boolean>("FallDistance", false));
    public Setting<Float> Height = this.register(new Setting<Float>("Height", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(300.0f), v -> this.fallDistance.getValue()));
    public Mode2 currentMode = Mode2.TOTEMS;
    public int totems = 0;
    public int crystals = 0;
    public int gapples = 0;
    public int potions = 0;
    public int chorusfruits = 0;
    public int lastTotemSlot = -1;
    public int lastGappleSlot = -1;
    public int lastCrystalSlot = -1;
    public int lastPotionSlot = -1;
    public int lastChorusSlot = -1;
    public int lastObbySlot = -1;
    public int lastWebSlot = -1;
    public boolean holdingCrystal = false;
    public boolean holdingTotem = false;
    public boolean holdingGapple = false;
    public boolean holdingPotion = false;
    public boolean holdingChorus = false;
    public boolean didSwitchThisTick = false;
    private boolean second = false;
    private boolean switchedForHealthReason = false;

    public Offhand() {
        super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
        instance = this;
    }

    public static Offhand getInstance() {
        if (instance == null) {
            instance = new Offhand();
        }
        return instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.objectMouseOver != null && event.pos == Offhand.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Offhand.mc.playerController.processRightClick(Offhand.mc.player, Offhand.mc.world, EnumHand.OFF_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Offhand.mc.player != null && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                Offhand.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        } else if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Offhand.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (Offhand.nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Offhand.fullNullCheck() && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            CPacketPlayerTryUseItem packet;
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Offhand.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet = (CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystals";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totems";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapples";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.POTIONITEM) {
            return "Potion";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT) {
            return "Chorus";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.holdingTotem = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
        this.holdingGapple = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
        this.holdingPotion = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.POTIONITEM;
        this.holdingChorus = Offhand.mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT;
        this.totems = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (this.holdingTotem) {
            this.totems += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        }
        this.crystals = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (this.holdingCrystal) {
            this.crystals += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        }
        this.gapples = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (this.holdingGapple) {
            this.gapples += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        }
        this.potions = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.POTIONITEM).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (this.holdingPotion) {
            this.potions += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.POTIONITEM).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        }
        this.chorusfruits = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.CHORUS_FRUIT).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (this.holdingChorus) {
            this.chorusfruits += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.CHORUS_FRUIT).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        }
        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue().booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.crystalGap.getValue().booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.pickGap.getValue().booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.swordCrouchPotion.getValue().booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && Offhand.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.currentMode = Mode2.POTION;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.pickCrouchChorus.getValue().booleanValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && Offhand.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.currentMode = Mode2.CHORUS;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.antiGappleFail.getValue().booleanValue() && this.currentMode == Mode2.GAPPLES && (!EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) <= this.crystalHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) <= this.crystalHoleHealth.getValue().floatValue())) {
            this.switchedForHealthReason = true;
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CHORUS && this.chorusfruits == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.POTION && this.potions == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && (!EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) <= this.crystalHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) <= this.crystalHoleHealth.getValue().floatValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth(Offhand.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue().booleanValue() && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if ((this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.GAPPLES) && Offhand.mc.player.fallDistance > this.Height.getValue().floatValue() && this.fallDistance.getValue().booleanValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (Offhand.mc.currentScreen instanceof GuiContainer && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0 || this.holdingTotem) break;
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                break;
            }
            case POTION: {
                if (this.potions <= 0 || this.holdingPotion) break;
                this.lastPotionSlot = InventoryUtil.findItemInventorySlot(Items.POTIONITEM, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastPotionSlot);
                this.putItemInOffhand(this.lastPotionSlot, lastSlot);
                break;
            }
            case CHORUS: {
                if (this.chorusfruits <= 0 || this.holdingChorus) break;
                this.lastChorusSlot = InventoryUtil.findItemInventorySlot(Items.CHORUS_FRUIT, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastChorusSlot);
                this.putItemInOffhand(this.lastChorusSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0 || this.holdingGapple) break;
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0 || this.holdingCrystal) break;
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            InventoryUtil.Task task = this.taskList.poll();
            if (task == null) continue;
            task.run();
            if (!task.isSwitching()) continue;
            this.didSwitchThisTick = true;
        }
    }

    private int getLastSlot(Item item, int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (item == Items.POTIONITEM) {
            return this.lastPotionSlot;
        }
        if (item == Items.CHORUS_FRUIT) {
            return this.lastChorusSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhand(int slotIn, int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(Mode2 mode) {
        this.currentMode = this.currentMode == mode ? Mode2.TOTEMS : mode;
    }

    public static enum Mode2 {
        TOTEMS,
        GAPPLES,
        CRYSTALS,
        POTION,
        CHORUS;

    }
}
