package me.primooctopus33.octohack.util;

import com.google.common.util.concurrent.AtomicDouble;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.combat.Surround;
import me.primooctopus33.octohack.util.CrystalUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import me.primooctopus33.octohack.util.TestUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BlockUtil
implements Util {
    public static List<Block> rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    public static final List<Block> unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
    public static List<Block> unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);
    public static List<Block> emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);

    public static List<BlockPos> getBlockSphere(float breakRange, Class clazz) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(BlockUtil.mc.world.getBlockState((BlockPos)pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }

    public static void placeClient(BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = BlockUtil.mc.player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemBlock) {
            int i;
            IBlockState placeState;
            ItemBlock itemBlock = (ItemBlock)((Object)stack.getItem());
            Block block = itemBlock.getBlock();
            IBlockState iblockstate = BlockUtil.mc.world.getBlockState(pos);
            Block iBlock = iblockstate.getBlock();
            if (!iBlock.isReplaceable(BlockUtil.mc.world, pos)) {
                pos = pos.offset(facing);
            }
            if (!stack.isEmpty() && BlockUtil.mc.player.canPlayerEdit(pos, facing, stack) && BlockUtil.mc.world.mayPlace(block, pos, false, facing, null) && itemBlock.placeBlockAt(stack, BlockUtil.mc.player, BlockUtil.mc.world, pos, facing, hitX, hitY, hitZ, placeState = block.getStateForPlacement(BlockUtil.mc.world, pos, facing, hitX, hitY, hitZ, i = itemBlock.getMetadata(stack.getMetadata()), BlockUtil.mc.player, hand))) {
                placeState = BlockUtil.mc.world.getBlockState(pos);
                SoundType soundtype = placeState.getBlock().getSoundType(placeState, BlockUtil.mc.world, pos, BlockUtil.mc.player);
                BlockUtil.mc.world.playSound(BlockUtil.mc.player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
                if (!BlockUtil.mc.player.isCreative()) {
                    stack.shrink(1);
                }
            }
        }
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false) || (blockState = BlockUtil.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static EnumFacing getRayTraceFacing(BlockPos pos) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getX() - 0.5, (double)pos.getX() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }

    public static boolean isBlockNotEmpty(BlockPos pos) {
        if (emptyBlocks.contains(BlockUtil.mc.world.getBlockState(pos).getBlock())) {
            Entity entity;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
            Iterator iterator = BlockUtil.mc.world.loadedEntityList.iterator();
            do {
                if (iterator.hasNext()) continue;
                return true;
            } while (!((entity = (Entity)iterator.next()) instanceof EntityLivingBase) || !axisAlignedBB.intersects(entity.getEntityBoundingBox()));
        }
        return false;
    }

    public static float getBlockDamage(BlockPos pos) {
        try {
            Field f = ReflectionHelper.findField(RenderGlobal.class, new String[]{"damagedBlocks", "field_72738_E"});
            f.setAccessible(true);
            HashMap map = (HashMap)f.get(Minecraft.getMinecraft().renderGlobal);
            for (DestroyBlockProgress destroyblockprogress : map.values()) {
                if (!destroyblockprogress.getPosition().equals(pos) || destroyblockprogress.getPartialBlockDamage() < 0 || destroyblockprogress.getPartialBlockDamage() > 10) continue;
                return (float)destroyblockprogress.getPartialBlockDamage() / 10.0f;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    public static void rotatePacket(double x, double y, double z) {
        double diffX = x - BlockUtil.mc.player.posX;
        double diffY = y - (BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight());
        double diffZ = z - BlockUtil.mc.player.posZ;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, BlockUtil.mc.player.onGround));
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return BlockUtil.isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!BlockUtil.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BlockUtil.getPossibleSides(pos)) {
            if (!BlockUtil.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        }
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
    }

    public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw, AtomicDouble Pitch, AtomicBoolean rotating) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (!(distanceSqHitVec <= MathUtil.square(range)) || !(distanceSqHitVec < distanceSqPosVec) || BlockUtil.mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) continue;
            if (rotate) {
                float[] rotations = RotationUtil.getLegitRotations(hitVec);
                Yaw.set(rotations[0]);
                Pitch.set(rotations[1]);
                rotating.set(true);
            }
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, side, hitVec, hand);
            BlockUtil.mc.player.swingArm(hand);
            BlockUtil.mc.rightClickDelayTimer = 4;
            break;
        }
    }

    public static boolean placeBlocks(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        boolean sneaking = false;
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVectorPacketInstant(hitVec);
        }
        BlockUtil.rightClickBlock(pos, hitVec, hand, side, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        return sneaking || isSneaking;
    }

    public static void clickBlock(BlockPos position, EnumFacing side, EnumHand hand, boolean packet) {
        if (packet) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position.offset(side), side.getOpposite(), hand, Float.intBitsToFloat(Float.floatToIntBits(17.0f)), Float.intBitsToFloat(Float.floatToIntBits(26.0f)), Float.intBitsToFloat(Float.floatToIntBits(3.0f))));
        } else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, position.offset(side), side.getOpposite(), new Vec3d(position), hand);
        }
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean extraPacket, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.calculateSide(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        float f = (float)(hitVec.x - (double)pos.getX());
        float f1 = (float)(hitVec.y - (double)pos.getY());
        float f2 = (float)(hitVec.z - (double)pos.getZ());
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        if (extraPacket) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbour, opposite, hand, f, f1, f2));
        }
        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        if (TestUtil.isBlockEmpty(pos) && Surround.emptyCheck.getValue().booleanValue()) {
            BlockUtil.placeClient(neighbour, hand, opposite, (float)hitVec.x, (float)hitVec.y, (float)hitVec.z);
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
            EnumActionResult actionResult = BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
            actionResult = EnumActionResult.SUCCESS;
            BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
        } else {
            BlockUtil.placeClient(neighbour, hand, opposite, (float)hitVec.x, (float)hitVec.y, (float)hitVec.z);
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
            EnumActionResult actionResult = BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
            actionResult = EnumActionResult.SUCCESS;
            BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour, opposite, hitVec, hand);
        }
        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        return sneaking || isSneaking;
    }

    public static boolean isBlocking(BlockPos pos, EntityPlayer player) {
        AxisAlignedBB posBB = new AxisAlignedBB(pos);
        return player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625).intersects(posBB);
    }

    public static EnumFacing calculateSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState offsetState = BlockUtil.mc.world.getBlockState(pos.offset(side));
            boolean activated = offsetState.getBlock().onBlockActivated(BlockUtil.mc.world, pos, offsetState, BlockUtil.mc.player, EnumHand.MAIN_HAND, side, 0.0f, 0.0f, 0.0f);
            if (activated) {
                mc.getConnection().sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            if (!offsetState.getBlock().canCollideCheck(offsetState, false) || offsetState.getMaterial().isReplaceable()) continue;
            return side;
        }
        return null;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        Command.sendMessage(side.toString());
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            OctoHack.rotationManager.lookAtVec3d(hitVec);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = BlockUtil.placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
        if (!isSneaking && sneaking) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public static boolean isBothHole(BlockPos blockPos) {
        for (BlockPos pos : BlockUtil.getTouchingBlocks(blockPos)) {
            IBlockState touchingState = BlockUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN)) continue;
            return false;
        }
        return true;
    }

    public static BlockPos[] getTouchingBlocks(BlockPos blockPos) {
        return new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
    }

    public static List<BlockPos> getSphereRealth(float radius, boolean ignoreAir) {
        ArrayList<BlockPos> sphere = new ArrayList<BlockPos>();
        BlockPos pos = new BlockPos(BlockUtil.mc.player.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = (int)radius;
        int x = posX - radiuss;
        while ((float)x <= (float)posX + radius) {
            int z = posZ - radiuss;
            while ((float)z <= (float)posZ + radius) {
                int y = posY - radiuss;
                while ((float)y < (float)posY + radius) {
                    if ((float)((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || BlockUtil.mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return sphere;
    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d(vec3d.x != 0.0 ? vec3d.x * 2.0 : vec3d.x, vec3d.y, vec3d.x != 0.0 ? vec3d.z : vec3d.z * 2.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x + 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z + 1.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x - 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z - 1.0), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z)};
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(BlockUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                int y = sphere ? cy - (int)r : cy;
                while (true) {
                    float f2;
                    float f = y;
                    float f3 = f2 = sphere ? (float)cy + r : (float)(cy + h);
                    if (!(f < f2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double)(r * r) && (!hollow || dist >= (double)((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            return (BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> BlockUtil.canPlaceCrystal(pos, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }

    public static boolean placeBlock2(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!BlockUtil.mc.player.isSneaking() && isSneaking) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        if (!BlockUtil.mc.player.isSneaking() && isSneaking) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            BlockUtil.mc.player.setSneaking(false);
            sneaking = false;
        }
        return sneaking || isSneaking;
    }

    public static boolean isInterceptedByOther(BlockPos blockPos) {
        for (Entity entity : BlockUtil.mc.world.loadedEntityList) {
            if (entity.equals(BlockUtil.mc.player) || !new AxisAlignedBB(blockPos).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    public static List<BlockPos> possiblePlacePosition(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> BlockUtil.canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
        return positions;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean bl, boolean bl2, boolean bl3) {
        boolean multiPlace = bl;
        boolean placeUnderBlock = bl2;
        BlockPos position = blockPos;
        if (CrystalUtil.mc.world.getBlockState(position).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(position).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        if (CrystalUtil.mc.world.getBlockState(position.add(0, 1, 0)).getBlock() != Blocks.AIR || !placeUnderBlock && CrystalUtil.mc.world.getBlockState(position.add(0, 2, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        if (multiPlace) {
            return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position.add(0, 1, 0))).isEmpty() && !placeUnderBlock && CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position.add(0, 2, 0))).isEmpty();
        }
        for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position.add(0, 1, 0)))) {
            if (entity instanceof EntityEnderCrystal) continue;
            return false;
        }
        if (!placeUnderBlock) {
            for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position.add(0, 2, 0)))) {
                boolean holePlace = placeUnderBlock;
                if (entity instanceof EntityEnderCrystal || holePlace && entity instanceof EntityPlayer) continue;
                return false;
            }
        }
        return true;
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        int old = BlockUtil.mc.player.inventory.currentItem;
        int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(crystal));
        }
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
        }
        if (swing) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static List<BlockPos> getSphere(float radius, boolean ignoreAir) {
        ArrayList<BlockPos> sphere = new ArrayList<BlockPos>();
        BlockPos pos = new BlockPos(BlockUtil.mc.player.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = (int)radius;
        int x = posX - radiuss;
        while ((float)x <= (float)posX + radius) {
            int z = posZ - radiuss;
            while ((float)z <= (float)posZ + radius) {
                int y = posY - radiuss;
                while ((float)y < (float)posY + radius) {
                    if ((float)((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || BlockUtil.mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return sphere;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (!oneDot15 && BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR || BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity.isDead || specialEntityCheck && entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            if (!oneDot15) {
                for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity.isDead || specialEntityCheck && entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        block7: {
            BlockPos boost = blockPos.add(0, 1, 0);
            BlockPos boost2 = blockPos.add(0, 2, 0);
            try {
                if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (specialEntityCheck) {
                    for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                        if (entity instanceof EntityEnderCrystal) continue;
                        return false;
                    }
                    for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                        if (entity instanceof EntityEnderCrystal) continue;
                        return false;
                    }
                    break block7;
                }
                return BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }
            catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).canCollideCheck(BlockUtil.getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return BlockUtil.getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }

    public static boolean isBlockAboveEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0, entity.posZ);
            return BlockUtil.isBlockSolid(pos);
        }
        return false;
    }

    public static void debugPos(String message, BlockPos pos) {
        Command.sendMessage(message + pos.getX() + "x, " + pos.getY() + "y, " + pos.getZ() + "z");
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d(pos);
    }

    public static BlockPos vec3dToPos(Vec3d vec3d) {
        return new BlockPos(vec3d);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = BlockUtil.getNeededRotations2(vec);
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], BlockUtil.mc.player.onGround));
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = BlockUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{BlockUtil.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - BlockUtil.mc.player.rotationYaw)), BlockUtil.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - BlockUtil.mc.player.rotationPitch))};
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false) || (blockState = BlockUtil.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            return side;
        }
        return null;
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && (double)pos.getZ() - BlockUtil.mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && (double)pos.getX() - BlockUtil.mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && (double)pos.getZ() - BlockUtil.mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || (double)pos.getX() - BlockUtil.mc.player.getPositionVector().x >= 0.0;
    }

    public static boolean isBlockBelowEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ);
            return BlockUtil.isBlockSolid(pos);
        }
        return false;
    }

    public static boolean isBlockSolid(BlockPos pos) {
        return !BlockUtil.isBlockUnSolid(pos);
    }

    public static boolean isBlockUnSolid(BlockPos pos) {
        return BlockUtil.isBlockUnSolid(BlockUtil.mc.world.getBlockState(pos).getBlock());
    }

    public static boolean isBlockUnSolid(Block block) {
        return unSolidBlocks.contains(block);
    }

    public static boolean isBlockUnSafe(Block block) {
        return unSafeBlocks.contains(block);
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return BlockUtil.convertVec3ds(entity.getPositionVector(), input);
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, BlockUtil.mc.world, pos) != -1.0f;
    }

    public static boolean isValidBlock(BlockPos pos) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        return !(block instanceof BlockLiquid) && block.getMaterial(null) != Material.AIR;
    }

    public static boolean isScaffoldPos(BlockPos pos) {
        return BlockUtil.mc.world.isAirBlock(pos) || BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER || BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || BlockUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX(), (float)pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return BlockUtil.rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return BlockUtil.rayTracePlaceCheck(pos, true);
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ));
    }

    public static ValidResult valid(BlockPos pos) {
        if (!BlockUtil.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
            return ValidResult.NoEntityCollision;
        }
        if (!BlockUtil.checkForNeighbours(pos)) {
            return ValidResult.NoNeighbors;
        }
        IBlockState l_State = BlockUtil.mc.world.getBlockState(pos);
        if (l_State.getBlock() == Blocks.AIR) {
            BlockPos[] l_Blocks;
            for (BlockPos l_Pos : l_Blocks = new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()}) {
                IBlockState l_State2 = BlockUtil.mc.world.getBlockState(l_Pos);
                if (l_State2.getBlock() == Blocks.AIR) continue;
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos neighbor = pos.offset(side);
                    if (!BlockUtil.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbor), false)) continue;
                    return ValidResult.Ok;
                }
            }
            return ValidResult.NoNeighbors;
        }
        return ValidResult.AlreadyBlockThere;
    }

    public static List<BlockPos> getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int)range;
        while ((double)x <= (double)cx + range) {
            int z = cz - (int)range;
            while ((double)z <= (double)cz + range) {
                int y = sphere ? cy - (int)range : cy;
                while (true) {
                    double d = y;
                    double d2 = sphere ? (double)cy + range : (double)cy + range;
                    if (!(d < d2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < range * range) || hollow && dist < (range - 1.0) * (range - 1.0))) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static boolean canPlaceInPosition(BlockPos position, boolean entityCheck, boolean sideCheck) {
        if (!BlockUtil.mc.world.getBlockState(position).getBlock().isReplaceable(BlockUtil.mc.world, position)) {
            return false;
        }
        if (entityCheck) {
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return false;
            }
        }
        return !sideCheck || BlockUtil.getPlaceableSide(position) != null;
    }

    public static boolean isPositionPlaceable(BlockPos position, boolean entityCheck, boolean sideCheck, boolean ignoreCrystals) {
        if (!BlockUtil.mc.world.getBlockState(position).getBlock().isReplaceable(BlockUtil.mc.world, position)) {
            return false;
        }
        if (entityCheck) {
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityEnderCrystal && ignoreCrystals) continue;
                return false;
            }
        }
        return !sideCheck || BlockUtil.getPlaceableSide(position) != null;
    }

    public static boolean isPositionPlaceable(BlockPos pos, boolean entityCheck, double distance) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return false;
        }
        if (entityCheck) {
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if ((double)BlockUtil.mc.player.getDistance(entity) > distance || entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!BlockUtil.hasNeighbour(blockPos)) {
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (!BlockUtil.hasNeighbour(neighbour)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) continue;
            return true;
        }
        return false;
    }

    public static void placeBlockss(BlockPos blockPos, boolean bl, boolean bl2, boolean bl3) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            boolean swing = bl;
            boolean packet = bl2;
            boolean rotate = bl3;
            BlockPos blockPos2 = blockPos;
            if (BlockUtil.mc.world.getBlockState(blockPos2.offset(enumFacing)).getBlock().equals(Blocks.AIR) || BlockUtil.isIntercepted(blockPos2)) continue;
            if (packet) {
                BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos2.offset(enumFacing), enumFacing.getOpposite(), EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(2.7f)), Float.intBitsToFloat(Float.floatToIntBits(3.8f)), Float.intBitsToFloat(Float.floatToIntBits(30.0f))));
            } else {
                BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, blockPos2.offset(enumFacing), enumFacing.getOpposite(), new Vec3d(blockPos2), EnumHand.MAIN_HAND);
            }
            if (swing) {
                BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if (rotate) {
                RotationUtil.faceVector(new Vec3d(blockPos2), true);
            }
            return;
        }
    }

    public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack, boolean swing) {
        if (TestUtil.isBlockEmpty(pos)) {
            EnumFacing[] facings;
            int old_slot = -1;
            if (slot != BlockUtil.mc.player.inventory.currentItem) {
                old_slot = BlockUtil.mc.player.inventory.currentItem;
                BlockUtil.mc.player.inventory.currentItem = slot;
            }
            for (EnumFacing f : facings = EnumFacing.values()) {
                Block neighborBlock = BlockUtil.mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d((double)pos.getX() + 0.5 + (double)f.getFrontOffsetX() * 0.5, (double)pos.getY() + 0.5 + (double)f.getFrontOffsetY() * 0.5, (double)pos.getZ() + 0.5 + (double)f.getFrontOffsetZ() * 0.5);
                if (emptyBlocks.contains(neighborBlock) || !(BlockUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25)) continue;
                float[] rot = new float[]{BlockUtil.mc.player.rotationYaw, BlockUtil.mc.player.rotationPitch};
                if (rotate) {
                    BlockUtil.rotatePacket(vec.x, vec.y, vec.z);
                }
                if (rightclickableBlocks.contains(neighborBlock)) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                if (rightclickableBlocks.contains(neighborBlock)) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (rotateBack) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], BlockUtil.mc.player.onGround));
                }
                if (swing) {
                    BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                if (old_slot != -1) {
                    BlockUtil.mc.player.inventory.currentItem = old_slot;
                }
                return true;
            }
            if (old_slot != -1) {
                BlockUtil.mc.player.inventory.currentItem = old_slot;
            }
        }
        return false;
    }

    public static void placeBlocksss(BlockPos blockPos, EnumHand enumHand, boolean bl) {
        boolean packet = bl;
        EnumHand hand = enumHand;
        BlockPos position = blockPos;
        if (!BlockUtil.mc.world.getBlockState(position).getBlock().isReplaceable(BlockUtil.mc.world, position)) {
            return;
        }
        if (BlockUtil.getPlaceableSide(position) == null) {
            return;
        }
        BlockUtil.clickBlock(position, BlockUtil.getPlaceableSide(position), hand, packet);
        BlockUtil.mc.player.connection.sendPacket(new CPacketAnimation(hand));
    }

    public static boolean isIntercepted(BlockPos blockPos) {
        for (Entity entity : BlockUtil.mc.world.loadedEntityList) {
            BlockPos blockPos2 = blockPos;
            if (entity instanceof EntityItem || entity instanceof EntityEnderCrystal || !new AxisAlignedBB(blockPos2).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BlockUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)((Object)stack.getItem())).getBlock();
            if (block instanceof BlockEnderChest) {
                return i;
            }
            if (!(block instanceof BlockObsidian)) continue;
            return i;
        }
        return -1;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ));
    }

    public static enum ValidResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok;

    }
}
