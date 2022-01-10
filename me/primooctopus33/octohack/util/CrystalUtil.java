package me.primooctopus33.octohack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.PlayerUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class CrystalUtil
implements Util {
    public static final AxisAlignedBB CRYSTAL_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);

    public static boolean canPlace(BlockPos pos) {
        if (!(CrystalUtil.mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops)) {
            return false;
        }
        if (!(CrystalUtil.mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian)) {
            return false;
        }
        return CrystalUtil.mc.world.checkNoEntityCollision(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0).offset(pos), null);
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

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        int old = BlockUtil.mc.player.inventory.currentItem;
        int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (swing) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static List<BlockPos> getSphere(float f, boolean bl, boolean bl2) {
        float range = f;
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        int x = CrystalUtil.mc.player.getPosition().getX() - (int)range;
        while ((float)x <= (float)CrystalUtil.mc.player.getPosition().getX() + range) {
            int z = CrystalUtil.mc.player.getPosition().getZ() - (int)range;
            while ((float)z <= (float)CrystalUtil.mc.player.getPosition().getZ() + range) {
                boolean sphere = bl;
                int y = sphere ? CrystalUtil.mc.player.getPosition().getY() - (int)range : CrystalUtil.mc.player.getPosition().getY();
                int n = y;
                while ((float)y < (float)CrystalUtil.mc.player.getPosition().getY() + range) {
                    boolean hollow = bl2;
                    double distance = (CrystalUtil.mc.player.getPosition().getX() - x) * (CrystalUtil.mc.player.getPosition().getX() - x) + (CrystalUtil.mc.player.getPosition().getZ() - z) * (CrystalUtil.mc.player.getPosition().getZ() - z) + (sphere ? (CrystalUtil.mc.player.getPosition().getY() - y) * (CrystalUtil.mc.player.getPosition().getY() - y) : 0);
                    if (distance < (double)(range * range) && (!hollow || distance >= ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(638.4060856917202) ^ 0x7F73F33FA9DAEA7FL)) * ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(13.015128470890444) ^ 0x7FDA07BEEB3F6D07L)))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return blocks;
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer targetPlayer = null;
        for (EntityPlayer player : new ArrayList(CrystalUtil.mc.world.playerEntities)) {
            if (CrystalUtil.mc.player.getDistanceSq(player) > MathUtil.square(range) || player == CrystalUtil.mc.player || OctoHack.friendManager.isFriend(player.getName()) || player.isDead || player.getHealth() <= Float.intBitsToFloat(Float.floatToIntBits(1.2784752E38f) ^ 0x7EC05D13)) continue;
            if (targetPlayer == null) {
                targetPlayer = player;
                continue;
            }
            if (CrystalUtil.mc.player.getDistanceSq(player) >= CrystalUtil.mc.player.getDistanceSq(targetPlayer)) continue;
            targetPlayer = player;
        }
        return targetPlayer;
    }

    public static EnumActionResult doPlace(BlockPos pos) {
        double dx = (double)pos.getX() + 0.5 - CrystalUtil.mc.player.posX;
        double dy = (double)(pos.getY() - 1) + 0.5 - CrystalUtil.mc.player.posY - 0.5 - (double)CrystalUtil.mc.player.getEyeHeight();
        double dz = (double)pos.getZ() + 0.5 - CrystalUtil.mc.player.posZ;
        double x = CrystalUtil.getDirection2D(dz, dx);
        double y = CrystalUtil.getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));
        Vec3d vec = CrystalUtil.getVectorForRotation(-y, x - 90.0);
        return CrystalUtil.mc.playerController.processRightClickBlock(CrystalUtil.mc.player, CrystalUtil.mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, CrystalUtil.mc.player.getActiveHand());
    }

    protected static final double getDirection2D(double dx, double dy) {
        double d;
        if (dy == 0.0) {
            d = dx > 0.0 ? 90.0 : -90.0;
        } else {
            d = Math.atan(dx / dy) * 57.2957796;
            if (dy < 0.0) {
                d = dx > 0.0 ? (d += 180.0) : (dx < 0.0 ? (d -= 180.0) : 180.0);
            }
        }
        return d;
    }

    protected static final Vec3d getVectorForRotation(double pitch, double yaw) {
        float f = MathHelper.cos((float)((float)(-yaw * 0.01745329238474369 - 3.1415927410125732)));
        float f1 = MathHelper.sin((float)((float)(-yaw * 0.01745329238474369 - 3.1415927410125732)));
        float f2 = -MathHelper.cos((float)((float)(-pitch * 0.01745329238474369)));
        float f3 = MathHelper.sin((float)((float)(-pitch * 0.01745329238474369)));
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static EnumActionResult placeCrystal(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx = (double)pos.getX() + 0.5 - CrystalUtil.mc.player.posX;
        double dy = (double)pos.getY() + 0.5 - CrystalUtil.mc.player.posY - 0.5 - (double)CrystalUtil.mc.player.getEyeHeight();
        double dz = (double)pos.getZ() + 0.5 - CrystalUtil.mc.player.posZ;
        double x = CrystalUtil.getDirection2D(dz, dx);
        double y = CrystalUtil.getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));
        Vec3d vec = CrystalUtil.getVectorForRotation(-y, x - 90.0);
        if (((ItemStack)CrystalUtil.mc.player.inventory.offHandInventory.get(0)).getItem().getClass().equals(Item.getItemById((int)426).getClass())) {
            return CrystalUtil.mc.playerController.processRightClickBlock(CrystalUtil.mc.player, CrystalUtil.mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.OFF_HAND);
        }
        if (InventoryUtil.pickItem(426, false) != -1) {
            InventoryUtil.setSlot(InventoryUtil.pickItem(426, false));
            return CrystalUtil.mc.playerController.processRightClickBlock(CrystalUtil.mc.player, CrystalUtil.mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
        }
        return EnumActionResult.FAIL;
    }

    public static boolean placeCrystalSilent(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx = (double)pos.getX() + 0.5 - CrystalUtil.mc.player.posX;
        double dy = (double)pos.getY() + 0.5 - CrystalUtil.mc.player.posY - 0.5 - (double)CrystalUtil.mc.player.getEyeHeight();
        double dz = (double)pos.getZ() + 0.5 - CrystalUtil.mc.player.posZ;
        double x = CrystalUtil.getDirection2D(dz, dx);
        double y = CrystalUtil.getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));
        int slot = InventoryUtil.pickItem(426, false);
        if (slot == -1 && ((ItemStack)CrystalUtil.mc.player.inventory.offHandInventory.get(0)).getItem() != Items.END_CRYSTAL) {
            return false;
        }
        Vec3d vec = CrystalUtil.getVectorForRotation(-y, x - 90.0);
        if (((ItemStack)CrystalUtil.mc.player.inventory.offHandInventory.get(0)).getItem() == Items.END_CRYSTAL) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
        } else if (InventoryUtil.pickItem(426, false) != -1) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        return true;
    }

    public static double getDamage(Vec3d pos, @Nullable Entity target) {
        double d9;
        double d7;
        double d5;
        double d13;
        double d12;
        Entity entity = target == null ? CrystalUtil.mc.player : target;
        float damage = 6.0f;
        float f3 = damage * 2.0f;
        Vec3d vec3d = pos;
        if (!entity.isImmuneToExplosions() && (d12 = entity.getDistance(pos.x, pos.y, pos.z) / (double)f3) <= 1.0 && (d13 = (double)MathHelper.sqrt((double)((d5 = entity.posX - pos.x) * d5 + (d7 = entity.posY + (double)entity.getEyeHeight() - pos.y) * d7 + (d9 = entity.posZ - pos.z) * d9))) != 0.0) {
            d5 /= d13;
            d7 /= d13;
            d9 /= d13;
            double d14 = CrystalUtil.mc.world.getBlockDensity(pos, entity.getEntityBoundingBox());
            double d10 = (1.0 - d12) * d14;
            return (int)((d10 * d10 + d10) / 2.0 * 7.0 * (double)f3 + 1.0);
        }
        return 0.0;
    }

    public static Boolean getArmorBreaker(EntityPlayer player, float percent) {
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR) {
                return true;
            }
            float armourPercent = (float)(stack.getMaxDamage() - stack.getItemDamage()) / (float)stack.getMaxDamage() * 100.0f;
            if (!(percent >= armourPercent) || stack.stackSize >= 2) continue;
            return true;
        }
        return false;
    }

    public static boolean rayTraceSolidCheck(Vec3d start, Vec3d end, boolean shouldIgnore) {
        if (!(Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z) || Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z))) {
            int currX = MathHelper.floor((double)start.x);
            int currY = MathHelper.floor((double)start.y);
            int currZ = MathHelper.floor((double)start.z);
            int endX = MathHelper.floor((double)end.x);
            int endY = MathHelper.floor((double)end.y);
            int endZ = MathHelper.floor((double)end.z);
            BlockPos blockPos = new BlockPos(currX, currY, currZ);
            IBlockState blockState = CrystalUtil.mc.world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (blockState.getCollisionBoundingBox(CrystalUtil.mc.world, blockPos) != Block.NULL_AABB && block.canCollideCheck(blockState, false) && (CrystalUtil.getBlocks().contains(block) || !shouldIgnore)) {
                return true;
            }
            double seDeltaX = end.x - start.x;
            double seDeltaY = end.y - start.y;
            double seDeltaZ = end.z - start.z;
            int steps = 200;
            while (steps-- >= 0) {
                EnumFacing facing;
                if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
                    return false;
                }
                if (currX == endX && currY == endY && currZ == endZ) {
                    return false;
                }
                boolean unboundedX = true;
                boolean unboundedY = true;
                boolean unboundedZ = true;
                double stepX = 999.0;
                double stepY = 999.0;
                double stepZ = 999.0;
                double deltaX = 999.0;
                double deltaY = 999.0;
                double deltaZ = 999.0;
                if (endX > currX) {
                    stepX = (double)currX + 1.0;
                } else if (endX < currX) {
                    stepX = currX;
                } else {
                    unboundedX = false;
                }
                if (endY > currY) {
                    stepY = (double)currY + 1.0;
                } else if (endY < currY) {
                    stepY = currY;
                } else {
                    unboundedY = false;
                }
                if (endZ > currZ) {
                    stepZ = (double)currZ + 1.0;
                } else if (endZ < currZ) {
                    stepZ = currZ;
                } else {
                    unboundedZ = false;
                }
                if (unboundedX) {
                    deltaX = (stepX - start.x) / seDeltaX;
                }
                if (unboundedY) {
                    deltaY = (stepY - start.y) / seDeltaY;
                }
                if (unboundedZ) {
                    deltaZ = (stepZ - start.z) / seDeltaZ;
                }
                if (deltaX == 0.0) {
                    deltaX = -1.0E-4;
                }
                if (deltaY == 0.0) {
                    deltaY = -1.0E-4;
                }
                if (deltaZ == 0.0) {
                    deltaZ = -1.0E-4;
                }
                if (deltaX < deltaY && deltaX < deltaZ) {
                    facing = endX > currX ? EnumFacing.WEST : EnumFacing.EAST;
                    start = new Vec3d(stepX, start.y + seDeltaY * deltaX, start.z + seDeltaZ * deltaX);
                } else if (deltaY < deltaZ) {
                    facing = endY > currY ? EnumFacing.DOWN : EnumFacing.UP;
                    start = new Vec3d(start.x + seDeltaX * deltaY, stepY, start.z + seDeltaZ * deltaY);
                } else {
                    facing = endZ > currZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    start = new Vec3d(start.x + seDeltaX * deltaZ, start.y + seDeltaY * deltaZ, stepZ);
                }
                if (!(block = (blockState = CrystalUtil.mc.world.getBlockState(blockPos = new BlockPos(currX = MathHelper.floor((double)start.x) - (facing == EnumFacing.EAST ? 1 : 0), currY = MathHelper.floor((double)start.y) - (facing == EnumFacing.UP ? 1 : 0), currZ = MathHelper.floor((double)start.z) - (facing == EnumFacing.SOUTH ? 1 : 0)))).getBlock()).canCollideCheck(blockState, false) || !CrystalUtil.getBlocks().contains(block) && shouldIgnore) continue;
                return true;
            }
        }
        return false;
    }

    public static float getDamageFromDifficulty(float damage) {
        switch (CrystalUtil.mc.world.getDifficulty()) {
            case PEACEFUL: {
                return 0.0f;
            }
            case EASY: {
                return Math.min(damage / 2.0f + 1.0f, damage);
            }
            case HARD: {
                return damage * 3.0f / 2.0f;
            }
        }
        return damage;
    }

    public static float calculateDamage(BlockPos pos, Entity target, boolean shouldIgnore) {
        return CrystalUtil.getExplosionDamage(target, new Vec3d((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5), 6.0f, shouldIgnore);
    }

    public static float calculateDamage(Entity crystal, Entity target, boolean shouldIgnore) {
        return CrystalUtil.getExplosionDamage(target, new Vec3d(crystal.posX, crystal.posY, crystal.posZ), 6.0f, shouldIgnore);
    }

    public static float getExplosionDamage(Entity targetEntity, Vec3d explosionPosition, float explosionPower, boolean shouldIgnore) {
        Vec3d entityPosition = new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
        if (targetEntity.isImmuneToExplosions()) {
            return 0.0f;
        }
        double distanceToSize = entityPosition.distanceTo(explosionPosition) / (double)(explosionPower *= 2.0f);
        double blockDensity = 0.0;
        AxisAlignedBB bbox = targetEntity.getEntityBoundingBox().offset(targetEntity.getPositionVector().subtract(entityPosition));
        Vec3d bboxDelta = new Vec3d(1.0 / ((bbox.maxX - bbox.minX) * 2.0 + 1.0), 1.0 / ((bbox.maxY - bbox.minY) * 2.0 + 1.0), 1.0 / ((bbox.maxZ - bbox.minZ) * 2.0 + 1.0));
        double xOff = (1.0 - Math.floor(1.0 / bboxDelta.x) * bboxDelta.x) / 2.0;
        double zOff = (1.0 - Math.floor(1.0 / bboxDelta.z) * bboxDelta.z) / 2.0;
        if (bboxDelta.x >= 0.0 && bboxDelta.y >= 0.0 && bboxDelta.z >= 0.0) {
            int nonSolid = 0;
            int total = 0;
            for (double x = 0.0; x <= 1.0; x += bboxDelta.x) {
                for (double y = 0.0; y <= 1.0; y += bboxDelta.y) {
                    for (double z = 0.0; z <= 1.0; z += bboxDelta.z) {
                        Vec3d startPos = new Vec3d(xOff + bbox.minX + (bbox.maxX - bbox.minX) * x, bbox.minY + (bbox.maxY - bbox.minY) * y, zOff + bbox.minZ + (bbox.maxZ - bbox.minZ) * z);
                        if (!CrystalUtil.rayTraceSolidCheck(startPos, explosionPosition, shouldIgnore)) {
                            ++nonSolid;
                        }
                        ++total;
                    }
                }
            }
            blockDensity = (double)nonSolid / (double)total;
        }
        double densityAdjust = (1.0 - distanceToSize) * blockDensity;
        float damage = (int)((densityAdjust * densityAdjust + densityAdjust) / 2.0 * 7.0 * (double)explosionPower + 1.0);
        if (targetEntity instanceof EntityLivingBase) {
            damage = CrystalUtil.getBlastReduction((EntityLivingBase)targetEntity, CrystalUtil.getDamageFromDifficulty(damage), new Explosion(CrystalUtil.mc.world, null, explosionPosition.x, explosionPosition.y, explosionPosition.z, explosionPower / 2.0f, false, true));
        }
        return damage;
    }

    public static List<Block> getBlocks() {
        return Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.BARRIER, Blocks.ENCHANTING_TABLE, Blocks.ENDER_CHEST, Blocks.END_PORTAL_FRAME, Blocks.BEACON, Blocks.ANVIL);
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        float enchantmentModifierDamage = 0.0f;
        try {
            enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)entity.getArmorInventoryList(), (DamageSource)DamageSource.causeExplosionDamage((Explosion)explosion));
        }
        catch (Exception exception) {
            // empty catch block
        }
        enchantmentModifierDamage = MathHelper.clamp((float)enchantmentModifierDamage, (float)0.0f, (float)20.0f);
        damage *= 1.0f - enchantmentModifierDamage / 25.0f;
        PotionEffect resistanceEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);
        if (entity.isPotionActive(MobEffects.RESISTANCE) && resistanceEffect != null) {
            damage = damage * (25.0f - (float)(resistanceEffect.getAmplifier() + 1) * 5.0f) / 25.0f;
        }
        damage = Math.max(damage, 0.0f);
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = CrystalUtil.mc.world.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)CrystalUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> CrystalUtil.mc.world.getBlockState((BlockPos)pos).getBlock() != Blocks.AIR).filter(pos -> CrystalUtil.canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
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
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
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

    public static boolean canSeePos(BlockPos pos) {
        return CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) == null;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean onepointThirteen) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (!onepointThirteen) {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || CrystalUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
                for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
                for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            } else {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0.0) {
            return entity;
        }
        EntityPlayer e = null;
        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(motionX, 2.0) + Math.pow(motionZ, 2.0) + Math.pow(motionY, 2.0));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        int i = 0;
        while ((double)i < x) {
            if (e == null) {
                if (CrystalUtil.isOnGround(0.0, 0.0, 0.0, entity)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= (double)0.98f;
                }
                e = CrystalUtil.placeValue(motionX, motionY, motionZ, (EntityPlayer)entity);
            } else {
                if (CrystalUtil.isOnGround(0.0, 0.0, 0.0, e)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= (double)0.98f;
                }
                e = CrystalUtil.placeValue(motionX, motionY, motionZ, e);
            }
            ++i;
        }
        return e;
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        try {
            double d3 = y;
            List list1 = CrystalUtil.mc.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
            if (y != 0.0) {
                int l = list1.size();
                for (int k = 0; k < l; ++k) {
                    y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
                }
            }
            return d3 != y && d3 < 0.0;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List list1 = CrystalUtil.mc.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
        if (y != 0.0) {
            int l = list1.size();
            for (int k = 0; k < l; ++k) {
                y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
            if (y != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, y, 0.0));
            }
        }
        if (x != 0.0) {
            int l5 = list1.size();
            for (int j5 = 0; j5 < l5; ++j5) {
                x = CrystalUtil.calculateXOffset(entity.getEntityBoundingBox(), x, (AxisAlignedBB)list1.get(j5));
            }
            if (x != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0, 0.0));
            }
        }
        if (z != 0.0) {
            int i6 = list1.size();
            for (int k5 = 0; k5 < i6; ++k5) {
                z = CrystalUtil.calculateZOffset(entity.getEntityBoundingBox(), z, (AxisAlignedBB)list1.get(k5));
            }
            if (z != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, 0.0, z));
            }
        }
        return entity;
    }

    public static double calculateXOffset(AxisAlignedBB other, double offsetX, AxisAlignedBB this1) {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ) {
            double d0;
            if (offsetX > 0.0 && other.maxX <= this1.minX) {
                double d1 = this1.minX - 0.3 - other.maxX;
                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0 && other.minX >= this1.maxX && (d0 = this1.maxX + 0.3 - other.minX) > offsetX) {
                offsetX = d0;
            }
        }
        return offsetX;
    }

    public static double calculateZOffset(AxisAlignedBB other, double offsetZ, AxisAlignedBB this1) {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY) {
            double d0;
            if (offsetZ > 0.0 && other.maxZ <= this1.minZ) {
                double d1 = this1.minZ - 0.3 - other.maxZ;
                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0 && other.minZ >= this1.maxZ && (d0 = this1.maxZ + 0.3 - other.minZ) > offsetZ) {
                offsetZ = d0;
            }
        }
        return offsetZ;
    }
}
