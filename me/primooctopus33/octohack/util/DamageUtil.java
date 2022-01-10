package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil
implements Util {
    public static int getRoundedDamage(ItemStack stack) {
        return (int)DamageUtil.getDamageInPercent(stack);
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (float)(DamageUtil.getItemDamage(stack) / stack.getMaxDamage()) * 100.0f;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                return true;
            }
            if (DamageUtil.getItemDamage(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        int size = DamageUtil.mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)DamageUtil.mc.world.playerEntities.get(i);
            if (EntityUtil.isntValid(player, range)) continue;
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (!(DamageUtil.mc.player.getDistanceSq(player) < DamageUtil.mc.player.getDistanceSq(currentTarget))) continue;
            currentTarget = player;
        }
        return currentTarget;
    }

    public static float calculateDamage(Vec3d pos, Entity entity) {
        return DamageUtil.calculateDamage(pos.x, pos.y, pos.z, entity);
    }

    public static float calculateDamage(Entity crystal, Entity entity) {
        return DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static boolean canBreakWeakness(EntityPlayer player) {
        int strengthAmp = 0;
        PotionEffect effect = DamageUtil.mc.player.getActivePotionEffect(MobEffects.STRENGTH);
        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }
        return !DamageUtil.mc.player.isPotionActive(MobEffects.WEAKNESS) || strengthAmp >= 1 || DamageUtil.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || DamageUtil.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe || DamageUtil.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe || DamageUtil.mc.player.getHeldItemMainhand().getItem() instanceof ItemSpade;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, boolean predict, int predictTicks) {
        AxisAlignedBB bb;
        Vec3d entityPos;
        if (predict) {
            entityPos = MathUtil.extrapolatePlayerPosition(entity, predictTicks);
            bb = entity.boundingBox.offset(-entity.posX, -entity.posY, -entity.posZ).offset(entityPos);
        } else {
            bb = entity.boundingBox;
            entityPos = entity.getPositionVector();
        }
        float doubleExplosionSize = 12.0f;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double distancedsize = entityPos.distanceTo(vec3d) / 12.0;
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, bb);
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float)((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = DamageUtil.getBlastReduction((EntityLivingBase)entity, DamageUtil.getDamageMultiplied(damage), new Explosion(DamageUtil.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    public static float calculateDamage(Entity crystal, Entity entity, boolean predict, int ticks) {
        return DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity, predict, ticks);
    }

    public static float calculateDamage(BlockPos pos, Entity entity, boolean predict, int ticks) {
        return DamageUtil.calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, entity, predict, ticks);
    }

    public static float calculateDamage(Vec3d pos, Entity entity, boolean predict, int ticks) {
        return DamageUtil.calculateDamage(pos.x, pos.y, pos.z, entity, predict, ticks);
    }

    public static boolean canTakeDamage(boolean suicide) {
        return !DamageUtil.mc.player.capabilities.isCreativeMode && !suicide;
    }

    public static boolean isNaked(EntityPlayer player) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null || piece.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = DamageUtil.getBlastReduction((EntityLivingBase)entity, DamageUtil.getDamageMultiplied(damage), new Explosion(DamageUtil.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage((Explosion)explosion);
            damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)ep.getTotalArmorValue(), (float)((float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)ep.getArmorInventoryList(), (DamageSource)ds);
            }
            catch (Exception exception) {
                // empty catch block
            }
            float f = MathHelper.clamp((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = DamageUtil.mc.world.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return DamageUtil.calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, entity);
    }

    public static int getCooldownByWeapon(EntityPlayer player) {
        Item item = player.getHeldItemMainhand().getItem();
        if (item instanceof ItemSword) {
            return 600;
        }
        if (item instanceof ItemPickaxe) {
            return 850;
        }
        if (item == Items.IRON_AXE) {
            return 1100;
        }
        if (item == Items.STONE_HOE) {
            return 500;
        }
        if (item == Items.IRON_HOE) {
            return 350;
        }
        if (item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
            return 1250;
        }
        if (item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE) {
            return 1000;
        }
        return 250;
    }
}
