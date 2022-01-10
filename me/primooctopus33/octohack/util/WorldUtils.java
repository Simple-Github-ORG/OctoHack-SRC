package me.primooctopus33.octohack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class WorldUtils
implements Util {
    public static final ArrayList<Block> empty = new ArrayList<Block>(Arrays.asList(Blocks.AIR, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.FLOWING_WATER, Blocks.WATER));
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);

    public static Block getBlock(BlockPos block) {
        return WorldUtils.mc.world.getBlockState(block).getBlock();
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                int y = sphere ? cy - (int)r : cy;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float)cy + r : (float)(cy + h);
                    if (!(f < f2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
                        circleblocks.add(new BlockPos(x, y + plus_y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!WorldUtils.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(WorldUtils.mc.world.getBlockState(neighbour), false) || (blockState = WorldUtils.mc.world.getBlockState(neighbour)).getBlock().getMaterial(blockState).isReplaceable()) continue;
            return side;
        }
        return null;
    }

    public static double getRange(Vec3d vec) {
        return WorldUtils.mc.player.getPositionVector().addVector(0.0, WorldUtils.mc.player.eyeHeight, 0.0).distanceTo(vec);
    }

    public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
        RayTraceResult result = WorldUtils.mc.world.rayTraceBlocks(new Vec3d(WorldUtils.mc.player.posX, WorldUtils.mc.player.posY + (double)WorldUtils.mc.player.getEyeHeight(), WorldUtils.mc.player.posZ), new Vec3d((double)placePosition.getX() + 0.5, (double)placePosition.getY() - 0.5, (double)placePosition.getZ() + 0.5));
        if (placePosition.getY() == 255) {
            return EnumFacing.DOWN;
        }
        if (rayTrace) {
            return result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        }
        return EnumFacing.UP;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return WorldUtils.mc.world.getBlockState(pos).getBlock().canCollideCheck(WorldUtils.mc.world.getBlockState(pos), false);
    }

    public static boolean isWithin(double distance, Vec3d vec, Vec3d vec2) {
        return vec.squareDistanceTo(vec2) <= distance * distance;
    }

    public static boolean isOutside(double distance, Vec3d vec, Vec3d vec2) {
        return vec.squareDistanceTo(vec2) > distance * distance;
    }

    private boolean place(BlockPos pos) {
        boolean isSneaking = WorldUtils.mc.player.isSneaking();
        Block block = WorldUtils.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockFire)) {
            return false;
        }
        EnumFacing side = WorldUtils.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!WorldUtils.canBeClicked(neighbour)) {
            return false;
        }
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = WorldUtils.mc.world.getBlockState(neighbour).getBlock();
        if (!isSneaking && blackList.contains(neighbourBlock)) {
            WorldUtils.mc.player.connection.sendPacket(new CPacketEntityAction(WorldUtils.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        WorldUtils.mc.playerController.processRightClickBlock(WorldUtils.mc.player, WorldUtils.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        WorldUtils.mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }
}
