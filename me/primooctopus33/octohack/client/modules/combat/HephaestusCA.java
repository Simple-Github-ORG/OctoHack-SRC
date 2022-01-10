package me.primooctopus33.octohack.client.modules.combat;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class HephaestusCA
extends Module {
    public static boolean togglePitch;
    public static boolean isSpoofingAngles;
    public static double yaw;
    public static double pitch;
    public final Setting<Boolean> place = this.register(new Setting<Boolean>("Place", true));
    public final Setting<Boolean> explode = this.register(new Setting<Boolean>("Explode", true));
    public final Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("Auto Switch", true));
    public final Setting<Boolean> antiWeakness = this.register(new Setting<Boolean>("Anti Weakness", true));
    public final Setting<Integer> hitTickDelay = this.register(new Setting<Integer>("Hit Delay", 2, 0, 20));
    public final Setting<Double> hitRange = this.register(new Setting<Double>("Hit Range", 5.5, 0.0, 6.0));
    public final Setting<Double> placeRange = this.register(new Setting<Double>("Place Range", 5.5, 0.0, 6.0));
    public final Setting<Double> minDamage = this.register(new Setting<Double>("Min Damage", 7.0, 0.0, 10.0));
    public final Setting<Boolean> spoofRotations = this.register(new Setting<Boolean>("Spoof Rotations", true));
    public final Setting<Boolean> rayTraceHit = this.register(new Setting<Boolean>("Ray Trace Hit", false));
    public final Setting<RenderMode> renderMode = this.register(new Setting<RenderMode>("Render Mode", RenderMode.BLOCK));
    private final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    public final Setting<Boolean> colorSync = this.register(new Setting<Object>("Sync", Boolean.valueOf(false), v -> this.render.getValue()));
    public final Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(false), v -> this.render.getValue()));
    public final Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Boolean> customOutline = this.register(new Setting<Object>("Custom Line", Boolean.valueOf(false), v -> this.outline.getValue() != false && this.render.getValue() != false));
    private final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    private final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    private final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("Box Alpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.render.getValue() != false));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("Line Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue() != false && this.render.getValue() != false));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    public BlockPos renderBlock;
    public EntityPlayer target;
    public boolean switchCooldown;
    public boolean isAttacking;
    public int oldSlot;
    public int newSlot;
    public int hitDelayCounter;
    @EventHandler
    public Listener<PacketEvent.Send> packetListener;

    public HephaestusCA() {
        super("HephaestusCA", "Automatically places and breaks crystals to kill your opponent", Module.Category.COMBAT, true, false, false);
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(HephaestusCA.mc.player.posX), Math.floor(HephaestusCA.mc.player.posY), Math.floor(HephaestusCA.mc.player.posZ));
    }

    static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = HephaestusCA.getBlastReduction((EntityLivingBase)entity, HephaestusCA.getDamageMultiplied(damage), new Explosion(HephaestusCA.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    private static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage((Explosion)explosion);
            damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)ep.getTotalArmorValue(), (float)((float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
            int k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)ep.getArmorInventoryList(), (DamageSource)ds);
            float f = MathHelper.clamp((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = HephaestusCA.mc.world.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = HephaestusCA.mc.player.rotationYaw;
            pitch = HephaestusCA.mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderBlock != null && !this.renderMode.getValue().equals((Object)RenderMode.NONE)) {
            RenderUtil.drawBoxESP(this.renderBlock, this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }

    @Override
    public void onUpdate() {
        if (HephaestusCA.mc.player != null) {
            EntityEnderCrystal crystal = HephaestusCA.mc.world.loadedEntityList.stream().filter(entityx -> entityx instanceof EntityEnderCrystal).map(entityx -> (EntityEnderCrystal)entityx).min(Comparator.comparing(c -> Float.valueOf(HephaestusCA.mc.player.getDistance((Entity)c)))).orElse(null);
            if (this.explode.getValue().booleanValue() && crystal != null && (double)HephaestusCA.mc.player.getDistance(crystal) <= this.hitRange.getValue() && this.rayTraceHitCheck(crystal)) {
                if (this.hitDelayCounter < this.hitTickDelay.getValue()) {
                    ++this.hitDelayCounter;
                } else {
                    this.hitDelayCounter = 0;
                    if (this.antiWeakness.getValue().booleanValue() && HephaestusCA.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                        if (!this.isAttacking) {
                            this.oldSlot = HephaestusCA.mc.player.inventory.currentItem;
                            this.isAttacking = true;
                        }
                        this.newSlot = -1;
                        for (int crystalSlot = 0; crystalSlot < 9; ++crystalSlot) {
                            ItemStack stack = HephaestusCA.mc.player.inventory.getStackInSlot(crystalSlot);
                            if (stack == ItemStack.EMPTY) continue;
                            if (stack.getItem() instanceof ItemSword) {
                                this.newSlot = crystalSlot;
                                break;
                            }
                            if (!(stack.getItem() instanceof ItemTool)) continue;
                            this.newSlot = crystalSlot;
                            break;
                        }
                        if (this.newSlot != -1) {
                            HephaestusCA.mc.player.inventory.currentItem = this.newSlot;
                            this.switchCooldown = true;
                        }
                    }
                    this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, HephaestusCA.mc.player);
                    HephaestusCA.mc.playerController.attackEntity(HephaestusCA.mc.player, crystal);
                    HephaestusCA.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            } else {
                int crystalSlot;
                HephaestusCA.resetRotation();
                if (this.oldSlot != -1) {
                    HephaestusCA.mc.player.inventory.currentItem = this.oldSlot;
                    this.oldSlot = -1;
                }
                this.isAttacking = false;
                int n = crystalSlot = HephaestusCA.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? HephaestusCA.mc.player.inventory.currentItem : -1;
                if (crystalSlot == -1) {
                    for (int l = 0; l < 9; ++l) {
                        if (HephaestusCA.mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) continue;
                        crystalSlot = l;
                        break;
                    }
                }
                boolean offhand = false;
                if (HephaestusCA.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                    offhand = true;
                } else if (crystalSlot == -1) {
                    return;
                }
                List entities = HephaestusCA.mc.world.playerEntities.stream().filter(entityPlayer -> !OctoHack.friendManager.isFriend(entityPlayer.getName())).sorted((entity1, entity2) -> Float.compare(HephaestusCA.mc.player.getDistance((Entity)entity1), HephaestusCA.mc.player.getDistance((Entity)entity2))).collect(Collectors.toList());
                List<BlockPos> blocks = this.findCrystalBlocks();
                BlockPos targetBlock = null;
                double targetBlockDamage = 0.0;
                this.target = null;
                block2: for (Entity entity : entities) {
                    if (entity == HephaestusCA.mc.player || !(entity instanceof EntityPlayer)) continue;
                    EntityPlayer testTarget = (EntityPlayer)entity;
                    if (testTarget.isDead || testTarget.getHealth() <= 0.0f) continue;
                    Iterator<BlockPos> var12 = blocks.iterator();
                    while (true) {
                        if (!var12.hasNext()) {
                            if (this.target == null) continue block2;
                            break block2;
                        }
                        BlockPos blockPos = var12.next();
                        if (testTarget.getDistanceSq(blockPos) >= 169.0) continue;
                        double targetDamage = HephaestusCA.calculateDamage((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5, testTarget);
                        double selfDamage = HephaestusCA.calculateDamage((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5, HephaestusCA.mc.player);
                        float healthTarget = testTarget.getHealth() + testTarget.getAbsorptionAmount();
                        float healthSelf = HephaestusCA.mc.player.getHealth() + HephaestusCA.mc.player.getAbsorptionAmount();
                        if (targetDamage < this.minDamage.getValue() || selfDamage >= (double)healthSelf - 0.5 || selfDamage > targetDamage && targetDamage < (double)healthTarget || !(targetDamage > targetBlockDamage)) continue;
                        targetBlock = blockPos;
                        targetBlockDamage = targetDamage;
                        this.target = testTarget;
                    }
                }
                if (this.target == null) {
                    this.renderBlock = null;
                    HephaestusCA.resetRotation();
                } else {
                    this.renderBlock = targetBlock;
                    if (this.place.getValue().booleanValue()) {
                        if (!offhand && HephaestusCA.mc.player.inventory.currentItem != crystalSlot) {
                            if (this.autoSwitch.getValue().booleanValue()) {
                                HephaestusCA.mc.player.inventory.currentItem = crystalSlot;
                                HephaestusCA.resetRotation();
                                this.switchCooldown = true;
                            }
                            return;
                        }
                        this.lookAtPacket((double)targetBlock.getX() + 0.5, (double)targetBlock.getY() - 0.5, (double)targetBlock.getZ() + 0.5, HephaestusCA.mc.player);
                        RayTraceResult result = HephaestusCA.mc.world.rayTraceBlocks(new Vec3d(HephaestusCA.mc.player.posX, HephaestusCA.mc.player.posY + (double)HephaestusCA.mc.player.getEyeHeight(), HephaestusCA.mc.player.posZ), new Vec3d((double)targetBlock.getX() + 0.5, (double)targetBlock.getY() - 0.5, (double)targetBlock.getZ() + 0.5));
                        EnumFacing f = result != null && result.sideHit != null ? result.sideHit : EnumFacing.UP;
                        if (this.switchCooldown) {
                            this.switchCooldown = false;
                            return;
                        }
                        HephaestusCA.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetBlock, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                    }
                    if (this.spoofRotations.getValue().booleanValue() && isSpoofingAngles) {
                        EntityPlayerSP var10000;
                        if (togglePitch) {
                            var10000 = HephaestusCA.mc.player;
                            var10000.rotationPitch = (float)((double)var10000.rotationPitch + 4.0E-4);
                            togglePitch = false;
                        } else {
                            var10000 = HephaestusCA.mc.player;
                            var10000.rotationPitch = (float)((double)var10000.rotationPitch - 4.0E-4);
                            togglePitch = true;
                        }
                    }
                }
            }
        }
    }

    private boolean rayTraceHitCheck(EntityEnderCrystal crystal) {
        return this.rayTraceHit.getValue() == false ? true : HephaestusCA.mc.player.canEntityBeSeen(crystal);
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = RotationUtil.calculateLookAt(px, py, pz, me);
        HephaestusCA.setYawAndPitch((float)v[0], (float)v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (HephaestusCA.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || HephaestusCA.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && HephaestusCA.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && HephaestusCA.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && HephaestusCA.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && HephaestusCA.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(HephaestusCA.getPlayerPos(), this.placeRange.getValue().floatValue(), this.placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    @Override
    public void onEnable() {
        this.hitDelayCounter = 0;
    }

    @Override
    public void onDisable() {
        this.renderBlock = null;
        this.target = null;
        HephaestusCA.resetRotation();
    }

    public String getHudInfo() {
        return this.target == null ? "" : this.target.getName().toUpperCase();
    }

    public static enum RenderMode {
        UP,
        BLOCK,
        NONE;

    }
}
