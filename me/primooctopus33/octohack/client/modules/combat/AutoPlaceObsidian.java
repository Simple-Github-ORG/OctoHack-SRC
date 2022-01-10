package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.CrystalUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;

public class AutoPlaceObsidian
extends Module {
    public final Setting<Integer> enemyRange = this.register(new Setting<Integer>("TargetRange", 5, 1, 12));
    public final Setting<Integer> placeRange = this.register(new Setting<Integer>("PlaceRange", 5, 1, 10));
    public final Setting<Integer> blockDistance = this.register(new Setting<Integer>("Block Distance", 4, 2, 6));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", false));
    public EntityPlayer target;
    public boolean placed = false;
    public int oldSlot = -1;
    public BlockPos currentPos = null;

    public AutoPlaceObsidian() {
        super("AutoPlaceObsidian", "Automatically places obsidian for fighting on terrain", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (AutoPlaceObsidian.mc.player == null || AutoPlaceObsidian.mc.world == null) {
            return;
        }
        this.oldSlot = AutoPlaceObsidian.mc.player.inventory.currentItem;
        this.target = (EntityPlayer)this.getClosest();
        int obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.target != null) {
            BlockPos pos;
            BlockPos playerPos = new BlockPos(Math.floor(this.target.posX), this.target.posY, Math.floor(this.target.posZ));
            if (!this.placed && (pos = this.getPos(this.target)) != null) {
                if (obiSlot != -1) {
                    AutoPlaceObsidian.mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                }
                BlockUtil.placeBlockss(pos, this.swing.getValue(), this.packet.getValue(), this.rotate.getValue());
                AutoPlaceObsidian.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
                this.placed = true;
                this.currentPos = pos;
            }
            if (this.currentPos != null && this.placed && ((double)AutoPlaceObsidian.mc.player.getDistance(this.target) > this.enemyRange.getValue().doubleValue() || AutoPlaceObsidian.mc.player.getDistanceSq(this.currentPos) > MathUtil.square(this.blockDistance.getValue().doubleValue()) || playerPos.getY() <= this.currentPos.getY() || BlockUtil.isIntercepted(this.currentPos.up()) || AutoPlaceObsidian.mc.world.getBlockState(this.currentPos.up()).getBlock() != Blocks.AIR)) {
                this.placed = false;
            }
        }
    }

    public BlockPos getPos(EntityPlayer entityPlayer) {
        BlockPos placePos = null;
        BlockPos playerPos = new BlockPos(Math.floor(this.target.posX), this.target.posY, Math.floor(this.target.posZ));
        double dist = MathUtil.square(this.placeRange.getValue().doubleValue());
        for (BlockPos pos : CrystalUtil.getSphere(this.placeRange.getValue().floatValue(), true, false)) {
            double pDist;
            if (pos.getY() >= playerPos.getY() || pos == playerPos || !this.canPlace(pos, true, true) || AutoPlaceObsidian.mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR || BlockUtil.isIntercepted(pos.up()) || BlockUtil.isIntercepted(pos.up()) || !((pDist = this.target.getDistanceSq(pos)) < dist)) continue;
            dist = pDist;
            placePos = pos;
        }
        return placePos;
    }

    public Entity getClosest() {
        Entity returnEntity = null;
        double dist = this.enemyRange.getValue().doubleValue();
        for (Entity entity : AutoPlaceObsidian.mc.world.loadedEntityList) {
            double pDist;
            if (!(entity instanceof EntityPlayer) || entity == null || (double)AutoPlaceObsidian.mc.player.getDistance(entity) > dist || entity == AutoPlaceObsidian.mc.player || !((pDist = (double)AutoPlaceObsidian.mc.player.getDistance(entity)) < dist)) continue;
            dist = pDist;
            returnEntity = entity;
        }
        return returnEntity;
    }

    public boolean canPlace(BlockPos blockPos, boolean bl, boolean bl2) {
        boolean bedrock = bl;
        boolean obsidian = bl2;
        BlockPos pos = blockPos;
        Block block = AutoPlaceObsidian.mc.world.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow || block instanceof BlockObsidian && obsidian || block == Blocks.BEDROCK && bedrock;
        {
        }
    }

    @Override
    public void onDisable() {
        this.placed = false;
    }
}
