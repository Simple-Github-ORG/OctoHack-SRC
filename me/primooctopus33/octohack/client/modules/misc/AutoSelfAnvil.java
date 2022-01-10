package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoSelfAnvil
extends Module {
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> onlyHole = this.register(new Setting<Boolean>("HoleOnly", false));
    private final Setting<Boolean> helpingBlocks = this.register(new Setting<Boolean>("HelpingBlocks", true));
    private final Setting<Boolean> chat = this.register(new Setting<Boolean>("Chat Msgs", true));
    private final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks/Tick", 2, 1, 8));
    private BlockPos placePos;
    private BlockPos playerPos;
    private int blockSlot;
    private int obbySlot;
    private int lastBlock;
    private int blocksThisTick;

    public AutoSelfAnvil() {
        super("AutoSelfAnvil", "Automatically drops an anvil on your head", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        this.playerPos = new BlockPos(AutoSelfAnvil.mc.player.posX, AutoSelfAnvil.mc.player.posY, AutoSelfAnvil.mc.player.posZ);
        this.placePos = this.playerPos.offset(EnumFacing.UP, 2);
        this.blockSlot = this.findBlockSlot();
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        this.lastBlock = AutoSelfAnvil.mc.player.inventory.currentItem;
        if (!this.doFirstChecks()) {
            this.disable();
        }
    }

    @Override
    public void onTick() {
        this.blocksThisTick = 0;
        this.doAutoSelfAnvil();
    }

    private void doAutoSelfAnvil() {
        if (this.helpingBlocks.getValue().booleanValue() && BlockUtil.isPositionPlaceable(this.placePos, false, true) == 2) {
            InventoryUtil.switchToHotbarSlot(this.obbySlot, false);
            this.doHelpBlocks();
        }
        if (this.blocksThisTick < this.blocksPerTick.getValue() && BlockUtil.isPositionPlaceable(this.placePos, false, true) == 3) {
            InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
            BlockUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, false);
            InventoryUtil.switchToHotbarSlot(this.lastBlock, false);
            AutoSelfAnvil.mc.player.connection.sendPacket(new CPacketEntityAction(AutoSelfAnvil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.disable();
        }
    }

    private void doHelpBlocks() {
        if (this.blocksThisTick >= this.blocksPerTick.getValue()) {
            return;
        }
        for (EnumFacing side1 : EnumFacing.values()) {
            if (side1 == EnumFacing.DOWN || BlockUtil.isPositionPlaceable(this.placePos.offset(side1), false, true) != 3) continue;
            BlockUtil.placeBlock(this.placePos.offset(side1), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, false);
            ++this.blocksThisTick;
            return;
        }
        for (EnumFacing side1 : EnumFacing.values()) {
            if (side1 == EnumFacing.DOWN) continue;
            for (EnumFacing side2 : EnumFacing.values()) {
                if (BlockUtil.isPositionPlaceable(this.placePos.offset(side1).offset(side2), false, true) != 3) continue;
                BlockUtil.placeBlock(this.placePos.offset(side1).offset(side2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, false);
                ++this.blocksThisTick;
                return;
            }
        }
        for (EnumFacing side1 : EnumFacing.values()) {
            for (EnumFacing side2 : EnumFacing.values()) {
                for (EnumFacing side3 : EnumFacing.values()) {
                    if (BlockUtil.isPositionPlaceable(this.placePos.offset(side1).offset(side2).offset(side3), false, true) != 3) continue;
                    BlockUtil.placeBlock(this.placePos.offset(side1).offset(side2).offset(side3), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, false);
                    ++this.blocksThisTick;
                    return;
                }
            }
        }
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack item = AutoSelfAnvil.mc.player.inventory.getStackInSlot(i);
            if (!(item.getItem() instanceof ItemBlock) || !((block = Block.getBlockFromItem((Item)AutoSelfAnvil.mc.player.inventory.getStackInSlot(i).getItem())) instanceof BlockFalling)) continue;
            return i;
        }
        return -1;
    }

    private boolean doFirstChecks() {
        int canPlace = BlockUtil.isPositionPlaceable(this.placePos, false, true);
        if (AutoSelfAnvil.fullNullCheck() || !AutoSelfAnvil.mc.world.isAirBlock(this.playerPos)) {
            return false;
        }
        if (!BlockUtil.isBothHole(this.playerPos) && this.onlyHole.getValue().booleanValue()) {
            return false;
        }
        if (this.blockSlot == -1) {
            if (this.chat.getValue().booleanValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cUnable to find anvils in your hotbar!");
            }
            return false;
        }
        if (canPlace == 2) {
            if (!this.helpingBlocks.getValue().booleanValue()) {
                if (this.chat.getValue().booleanValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cUnable to find a position to place!");
                }
                return false;
            }
            if (this.obbySlot == -1) {
                if (this.chat.getValue().booleanValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cUnable to find obsidian in your hotbar!");
                }
                return false;
            }
        } else if (canPlace != 3) {
            if (this.chat.getValue().booleanValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cNot Enough Room!");
            }
            return false;
        }
        return true;
    }
}
