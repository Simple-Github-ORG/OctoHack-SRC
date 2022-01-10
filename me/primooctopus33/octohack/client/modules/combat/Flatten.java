package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Flatten
extends Module {
    public final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks Per Tick", 8, 1, 30));
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Simple));
    public final Setting<Float> targetRange = this.register(new Setting<Float>("Target Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public final Setting<Boolean> airPlace = this.register(new Setting<Boolean>("Air Place", true));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet Place", true));
    public final Setting<Boolean> autoDisable = this.register(new Setting<Boolean>("Auto Disable", true));
    public final Setting<Boolean> targetRangeCheck = this.register(new Setting<Boolean>("Target Range Check", true));
    public final Vec3d[] default1 = new Vec3d[]{new Vec3d(0.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0)};
    public final Vec3d[] default2 = new Vec3d[]{new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0)};
    public EntityPlayer target;
    public int offsetStep = 0;
    public int oldSlot = -1;

    public Flatten() {
        super("Flatten", "Places blocks under other players feet to stop them from getting to safe spots.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.oldSlot = Flatten.mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        this.oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        this.target = this.findClosestTarget();
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.target == null && this.targetRangeCheck.getValue().booleanValue()) {
            this.disable();
            return;
        }
        if (this.target == null) {
            return;
        }
        ArrayList placements = new ArrayList();
        if (this.mode.getValue() == Mode.Simple) {
            Collections.addAll(placements, this.default1);
        }
        if (this.mode.getValue() == Mode.Large) {
            Collections.addAll(placements, this.default2);
        }
        int blocks_placed = 0;
        while (blocks_placed < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= placements.size()) {
                this.offsetStep = 0;
                break;
            }
            boolean placing = true;
            BlockPos offset_pos = new BlockPos((Vec3d)placements.get(this.offsetStep));
            BlockPos placePos = new BlockPos(Objects.requireNonNull(this.target).getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = Flatten.mc.world.getBlockState(placePos).getMaterial().isReplaceable();
            for (Entity entity : Flatten.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                should_try_place = false;
                break;
            }
            if (should_try_place) {
                this.place(placePos, obbySlot, this.oldSlot);
                ++blocks_placed;
            }
            ++this.offsetStep;
        }
        if (this.autoDisable.getValue().booleanValue()) {
            this.disable();
        }
    }

    public void place(BlockPos pos, int slot, int oldSlot) {
        Flatten.mc.player.inventory.currentItem = slot;
        Flatten.mc.playerController.updateController();
        if (this.airPlace.getValue().booleanValue()) {
            if (this.canPlace(pos) && this.target.onGround) {
                this.placeBlock(pos, EnumFacing.DOWN);
            }
        } else {
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, Flatten.mc.player.isSneaking());
        }
        Flatten.mc.player.inventory.currentItem = oldSlot;
        Flatten.mc.playerController.updateController();
    }

    public EntityPlayer findClosestTarget() {
        if (Flatten.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : Flatten.mc.world.playerEntities) {
            if (target == Flatten.mc.player || !target.isEntityAlive() || OctoHack.friendManager.isFriend(target.getName()) || target.getHealth() <= 0.0f || Flatten.mc.player.getDistance(target) > this.targetRange.getValue().floatValue() || closestTarget != null && Flatten.mc.player.getDistance(target) > Flatten.mc.player.getDistance(closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    public boolean canPlace(BlockPos pos) {
        Block block = Flatten.mc.world.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    public void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Flatten.mc.playerController.processRightClickBlock(Flatten.mc.player, Flatten.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Flatten.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static enum Mode {
        Simple,
        Large;

    }
}
