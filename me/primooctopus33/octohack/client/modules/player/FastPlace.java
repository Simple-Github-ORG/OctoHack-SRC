package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class FastPlace
extends Module {
    private Setting<Boolean> all = this.register(new Setting<Boolean>("All", false));
    private Setting<Boolean> obby = this.register(new Setting<Object>("Obsidian", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> crystals = this.register(new Setting<Object>("Crystals", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> exp = this.register(new Setting<Object>("Experience", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> PacketCrystal = this.register(new Setting<Boolean>("PacketCrystal", false));
    private Setting<Integer> useDelay = this.register(new Setting<Integer>("Use Delay", 1, 0, 10));
    private BlockPos mousePos = null;

    public FastPlace() {
        super("FastPlace", "Allows you to use items faster", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (FastPlace.fullNullCheck()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.exp.getValue().booleanValue()) {
            FastPlace.mc.rightClickDelayTimer = this.useDelay.getValue();
        }
        if (InventoryUtil.holdingItem(BlockObsidian.class) && this.obby.getValue().booleanValue()) {
            FastPlace.mc.rightClickDelayTimer = this.useDelay.getValue();
        }
        if (this.all.getValue().booleanValue()) {
            FastPlace.mc.rightClickDelayTimer = this.useDelay.getValue();
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (this.crystals.getValue().booleanValue() || this.all.getValue().booleanValue())) {
            FastPlace.mc.rightClickDelayTimer = this.useDelay.getValue();
        }
        if (this.PacketCrystal.getValue().booleanValue() && FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            boolean offhand;
            boolean bl = offhand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            if (offhand || FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                RayTraceResult result = FastPlace.mc.objectMouseOver;
                if (result == null) {
                    return;
                }
                switch (result.typeOfHit) {
                    case MISS: {
                        this.mousePos = null;
                        break;
                    }
                    case BLOCK: {
                        this.mousePos = FastPlace.mc.objectMouseOver.getBlockPos();
                        break;
                    }
                    case ENTITY: {
                        Entity entity;
                        if (this.mousePos == null || (entity = result.entityHit) == null || !this.mousePos.equals(new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ))) break;
                        FastPlace.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                    }
                }
            }
        }
    }
}
