package me.primooctopus33.octohack.client.modules.misc;

import java.util.Comparator;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.PlayerUtil;
import me.primooctopus33.octohack.util.TestUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoBedTrap
extends Module {
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 0, 10));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 1, 0, 200));
    public final Setting<Boolean> extraPacket = this.register(new Setting<Boolean>("Extra Packet", false));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public final Timer timer = new Timer();

    public AutoBedTrap() {
        super("AutoBedTrap", "Automatically traps beds around you in obsidian", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onUpdate() {
        BlockPos bedPos = BlockUtil.getSphere(PlayerUtil.getPlayerPosFloored(), this.range.getValue().intValue(), this.range.getValue(), false, true, 0).stream().filter(pos -> this.isBed((BlockPos)pos)).min(Comparator.comparing(pos -> EntityUtil.getDistPlayerToBlock(AutoBedTrap.mc.player, pos))).orElse(null);
        if (bedPos == null || AutoBedTrap.fullNullCheck()) {
            return;
        }
        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            return;
        }
        this.trapBed();
    }

    public void trapBed() {
        BlockPos bedPos = BlockUtil.getSphere(PlayerUtil.getPlayerPosFloored(), this.range.getValue().intValue(), this.range.getValue(), false, true, 0).stream().filter(pos -> this.isBed((BlockPos)pos)).min(Comparator.comparing(pos -> EntityUtil.getDistPlayerToBlock(AutoBedTrap.mc.player, pos))).orElse(null);
        BlockPos bedX = new BlockPos(bedPos.getX() + 1, bedPos.getY(), bedPos.getZ());
        BlockPos bedXMinus = new BlockPos(bedPos.getX() - 1, bedPos.getY(), bedPos.getZ());
        BlockPos bedZ = new BlockPos(bedPos.getX(), bedPos.getY(), bedPos.getZ() + 1);
        BlockPos bedZMinus = new BlockPos(bedPos.getX(), bedPos.getY(), bedPos.getZ() - 1);
        BlockPos bedY = new BlockPos(bedPos.getX(), bedPos.getY() + 1, bedPos.getZ());
        BlockPos bedYMinus = new BlockPos(bedPos.getX(), bedPos.getY() - 1, bedPos.getZ());
        if (TestUtil.canPlaceBlock(bedX)) {
            this.placeBlock(bedX, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (TestUtil.canPlaceBlock(bedXMinus)) {
            this.placeBlock(bedXMinus, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (TestUtil.canPlaceBlock(bedZ)) {
            this.placeBlock(bedZ, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (TestUtil.canPlaceBlock(bedZMinus)) {
            this.placeBlock(bedZMinus, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (TestUtil.canPlaceBlock(bedY)) {
            this.placeBlock(bedY, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (TestUtil.canPlaceBlock(bedYMinus)) {
            this.placeBlock(bedYMinus, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
    }

    public void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean sneaking) {
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int oldSlot = AutoBedTrap.mc.player.inventory.currentItem;
        AutoBedTrap.mc.player.inventory.currentItem = obbySlot;
        AutoBedTrap.mc.player.connection.sendPacket(new CPacketEntityAction(AutoBedTrap.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        if (this.timer.passedMs(this.delay.getValue().intValue())) {
            BlockUtil.placeBlock(pos, hand, rotate, packet, this.extraPacket.getValue(), sneaking);
            this.timer.reset();
        }
        AutoBedTrap.mc.player.connection.sendPacket(new CPacketEntityAction(AutoBedTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        AutoBedTrap.mc.player.inventory.currentItem = oldSlot;
    }

    private boolean isBed(BlockPos pos) {
        IBlockState blockState = AutoBedTrap.mc.world.getBlockState(pos);
        return blockState.getBlock() instanceof BlockBed;
    }
}
