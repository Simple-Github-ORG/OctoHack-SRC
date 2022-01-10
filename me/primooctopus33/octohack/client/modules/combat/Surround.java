package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.PlayerUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import me.primooctopus33.octohack.util.TestUtil;
import me.primooctopus33.octohack.util.Timer;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround
extends Module {
    public static Setting<Boolean> rotate;
    public static Setting<Boolean> packet;
    public static Setting<Boolean> ground;
    public static Setting<Integer> noFullExtend;
    public static Setting<Boolean> dynamicEntityExtend;
    public static Setting<Boolean> antiAnvil;
    public static Setting<Boolean> blockClear;
    public static Setting<Integer> blockClearRange;
    public static Setting<CrystalClear> crystalClear;
    public static Setting<Integer> crystalClearRange;
    public static Setting<Float> maxSelfDamage;
    public static Setting<Boolean> swingCrystalClear;
    public static Setting<Boolean> rotateCrystal;
    public static Setting<Boolean> packetCrystal;
    public static Setting<Boolean> extraPacket;
    public static Setting<Boolean> emptyCheck;
    public static Setting<Boolean> shift;
    public static Setting<Boolean> center;
    public static Setting<Sensitivity> sensitivity;
    public static Setting<AntiCity> antiCity;
    public static Setting<Integer> delay;
    public static Setting<Integer> bps;
    public static Setting<Boolean> yDisable;
    public static Setting<Boolean> disables;
    public static Setting<Boolean> toggleOnStep;
    public BlockPos startPos;
    public EnumFacing facing = null;
    public static Timer timer;
    private final BlockPos[] pos = new BlockPos[]{new BlockPos(0, 0, 1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};

    @Override
    public void onLogout() {
        this.disable();
    }

    public Surround() {
        super("Surround", "Surrounds you with obsidian at your feet to block crystal damage", Module.Category.COMBAT, true, false, false);
        rotate = this.register(new Setting<Boolean>("Rotate", true));
        packet = this.register(new Setting<Boolean>("Packet Place", true));
        ground = this.register(new Setting<Boolean>("Build Base", true));
        noFullExtend = this.register(new Setting<Integer>("No Full Extend", 1, -10, 10));
        dynamicEntityExtend = this.register(new Setting<Boolean>("Dynamic Entity Extend", true));
        antiAnvil = this.register(new Setting<Boolean>("Anti Anvil", true));
        blockClear = this.register(new Setting<Boolean>("Block Clear", true));
        blockClearRange = this.register(new Setting<Integer>("Block Clear Range", 3, 1, 10));
        crystalClear = this.register(new Setting<CrystalClear>("Crystal Clear", CrystalClear.Toggle));
        crystalClearRange = this.register(new Setting<Integer>("Crystal Clear Range", 3, 1, 10));
        maxSelfDamage = this.register(new Setting<Float>("Max Self Damage", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
        swingCrystalClear = this.register(new Setting<Boolean>("Swing Crystal Clear", true));
        rotateCrystal = this.register(new Setting<Boolean>("Rotate Crystal", false));
        packetCrystal = this.register(new Setting<Boolean>("Packet Crystal", true));
        extraPacket = this.register(new Setting<Boolean>("Extra Packet", false));
        emptyCheck = this.register(new Setting<Boolean>("Empty Check", false));
        shift = this.register(new Setting<Boolean>("Place On Shift", false));
        center = this.register(new Setting<Boolean>("Center", false));
        sensitivity = this.register(new Setting<Sensitivity>("Sensitivity", Sensitivity.High));
        antiCity = this.register(new Setting<AntiCity>("Anti City", AntiCity.Smart));
        delay = this.register(new Setting<Integer>("Delay", 0, 0, 25));
        bps = this.register(new Setting<Integer>("Blocks Per Place", 16, 1, 20));
        yDisable = this.register(new Setting<Boolean>("Y Disable", true));
        disables = this.register(new Setting<Boolean>("Jump Disable", true));
        toggleOnStep = this.register(new Setting<Boolean>("Step Disable", true));
        timer = new Timer();
    }

    @Override
    public void onEnable() {
        this.startPos = new BlockPos(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posZ);
        BlockPos centerPos = new BlockPos((double)PlayerUtil.getPlayerPosFloored().getX() + 0.5, (double)PlayerUtil.getPlayerPosFloored().getY(), (double)PlayerUtil.getPlayerPosFloored().getZ() + 0.5);
        if (center.getValue().booleanValue()) {
            OctoHack.positionManager.setPositionPacket((double)centerPos.getX() + 0.5, centerPos.getY(), (double)centerPos.getZ() + 0.5, true, true, true);
            OctoHack.positionManager.setPositionPacket((double)centerPos.getX() + 0.5, centerPos.getY(), (double)centerPos.getZ() + 0.5, true, true, true);
            OctoHack.positionManager.setPositionPacket((double)centerPos.getX() + 0.5, centerPos.getY(), (double)centerPos.getZ() + 0.5, true, true, true);
        }
        if (crystalClear.getValue() == CrystalClear.Toggle) {
            for (Entity entities : Surround.mc.world.loadedEntityList) {
                if (!(entities instanceof EntityEnderCrystal) || !(Surround.mc.player.getDistance(entities.posX, entities.posY, entities.posZ) < (double)crystalClearRange.getValue().intValue()) || EntityUtil.isSafe(Surround.mc.player)) continue;
                EntityUtil.attackEntity(entities, packetCrystal.getValue(), swingCrystalClear.getValue());
            }
        }
    }

    @Override
    public void onUpdate() {
        if (Surround.nullCheck()) {
            return;
        }
        if (yDisable.getValue().booleanValue() && Surround.mc.player.posY != (double)this.startPos.getY()) {
            OctoHack.moduleManager.disableModule("Surround");
        }
        if (disables.getValue().booleanValue() && Surround.mc.gameSettings.keyBindJump.isKeyDown()) {
            OctoHack.moduleManager.disableModule("Surround");
        }
        if (toggleOnStep.getValue().booleanValue() && OctoHack.moduleManager.isModuleEnabled("Step")) {
            OctoHack.moduleManager.disableModule("Surround");
        }
        if (shift.getValue().booleanValue() && !Surround.mc.gameSettings.keyBindSneak.isKeyDown()) {
            return;
        }
        if (shift.getValue().booleanValue() && Surround.mc.gameSettings.keyBindSneak.isKeyDown() && (Surround.mc.player.isElytraFlying() || Surround.mc.player.capabilities.isFlying)) {
            return;
        }
        BlockPos playerPos = PlayerUtil.getPlayerPosFloored();
        int placed = 0;
        int oldslot = Surround.mc.player.inventory.currentItem;
        ArrayList<BlockPos> blocks = EntityUtil.getPos(0.0, 0.0, 0.0, Surround.mc.player);
        if (blocks.size() == 2) {
            Block block1;
            BlockPos[] block;
            BlockPos pos2 = blocks.get(1);
            BlockPos pos = blocks.get(0);
            BlockPos[] offsets = new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west()};
            BlockPos[] offsets2 = new BlockPos[]{pos2.north(), pos2.south(), pos2.east(), pos2.west()};
            if (WorldUtils.empty.contains(WorldUtils.getBlock(pos2.down())) && !this.intersectsWithEntity(pos)) {
                this.placeBlock(pos2.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            if (placed > bps.getValue() - 1) {
                return;
            }
            if (WorldUtils.empty.contains(WorldUtils.getBlock(pos.down())) && !this.intersectsWithEntity(pos)) {
                this.placeBlock(pos.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            for (BlockPos off : offsets) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
            for (BlockPos off : offsets2) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
        } else if (blocks.size() == 4) {
            Block block1;
            Block block;
            BlockPos posA = blocks.get(1);
            BlockPos posB = blocks.get(2);
            BlockPos posC = blocks.get(3);
            BlockPos posD = blocks.get(0);
            BlockPos[] offsetsA = new BlockPos[]{posA.north(), posA.south(), posA.east(), posA.west()};
            BlockPos[] offsetsB = new BlockPos[]{posB.north(), posB.south(), posB.east(), posB.west()};
            BlockPos[] offsetsC = new BlockPos[]{posC.north(), posC.south(), posC.east(), posC.west()};
            BlockPos[] offsetsD = new BlockPos[]{posD.north(), posD.south(), posD.east(), posD.west()};
            if (WorldUtils.empty.contains(WorldUtils.getBlock(posA.down())) && !this.intersectsWithEntity(posA)) {
                this.placeBlock(posA.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            if (placed > bps.getValue() - 1) {
                return;
            }
            if (WorldUtils.empty.contains(WorldUtils.getBlock(posB.down())) && !this.intersectsWithEntity(posB)) {
                this.placeBlock(posB.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            if (placed > bps.getValue() - 1) {
                return;
            }
            if (WorldUtils.empty.contains(WorldUtils.getBlock(posC.down())) && !this.intersectsWithEntity(posC)) {
                this.placeBlock(posC.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            if (placed > bps.getValue() - 1) {
                return;
            }
            if (WorldUtils.empty.contains(WorldUtils.getBlock(posD.down())) && !this.intersectsWithEntity(posD)) {
                this.placeBlock(posD.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            for (BlockPos off : offsetsA) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
            for (BlockPos off : offsetsB) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
            for (BlockPos off : offsetsC) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
            for (BlockPos off : offsetsD) {
                if (placed > bps.getValue() - 1) continue;
                block = WorldUtils.getBlock(off);
                block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
        } else if (blocks.size() == 1) {
            BlockPos pos = blocks.get(0);
            BlockPos[] offsets = new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west()};
            if (WorldUtils.empty.contains(WorldUtils.getBlock(pos.down())) && !this.intersectsWithEntity(pos)) {
                this.placeBlock(pos.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                ++placed;
            }
            for (BlockPos off : offsets) {
                if (placed > bps.getValue() - 1) continue;
                Block block = WorldUtils.getBlock(off);
                Block block1 = WorldUtils.getBlock(off.down());
                if (WorldUtils.empty.contains(block1) && ground.getValue().booleanValue() && !this.intersectsWithEntity(off.down())) {
                    this.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    ++placed;
                } else if (this.intersectsWithEntity(off.down()) && dynamicEntityExtend.getValue().booleanValue() && ground.getValue().booleanValue()) {
                    this.doDynamicExtend(off.down());
                }
                BlockPos floorPos = new BlockPos(Surround.mc.player.posX, Surround.mc.player.posY - 1.0, Surround.mc.player.posZ);
                if (ground.getValue().booleanValue() && Surround.mc.world.getBlockState(floorPos).getBlock() == Blocks.AIR) {
                    this.placeBlock(floorPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                }
                if (placed > bps.getValue() - 1) continue;
                if (blockClear.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_WIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.UNLIT_REDSTONE_TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TORCH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.GRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TALLGRASS || Surround.mc.world.getBlockState(off).getBlock() == Blocks.DEADBUSH || Surround.mc.world.getBlockState(off).getBlock() == Blocks.TRIPWIRE || Surround.mc.world.getBlockState(off).getBlock() == Blocks.WHEAT) {
                    this.doClearBlocks(off);
                }
                if (antiAnvil.getValue().booleanValue() && Surround.mc.world.getBlockState(off).getBlock() == Blocks.ANVIL) {
                    this.doBlockExtend(off);
                }
                if (WorldUtils.empty.contains(block) && !this.intersectsWithEntity(off)) {
                    if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.TRAPPED_CHEST || Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.SKULL) {
                        this.placeBlock(new BlockPos(off.getX(), off.getY() + noFullExtend.getValue(), off.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    } else {
                        this.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    }
                    ++placed;
                } else if (this.intersectsWithEntity(off) && dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(off);
                }
                if (BlockUtil.getBlockDamage(off) == 0.0f || antiCity.getValue() != AntiCity.Smart) continue;
                this.doBlockExtend(off);
            }
        } else {
            Vec3d Center = new Vec3d((double)PlayerUtil.getPlayerPosFloored().getX() + 0.5, PlayerUtil.getPlayerPosFloored().getY(), (double)PlayerUtil.getPlayerPosFloored().getZ() + 0.5);
            for (BlockPos pos : this.pos) {
                if (placed > bps.getValue() - 1) continue;
                if (WorldUtils.empty.contains(WorldUtils.getBlock(playerPos.add(pos).down())) && !this.intersectsWithEntity(pos.down())) {
                    ++placed;
                    this.placeBlock(playerPos.add(pos).down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                }
                if (placed > bps.getValue() - 1 || !WorldUtils.empty.contains(WorldUtils.getBlock(playerPos.add(pos))) || this.intersectsWithEntity(pos)) continue;
                ++placed;
                this.placeBlock(playerPos.add(pos), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
        }
        if (placed == 0) {
            // empty if block
        }
        if (crystalClear.getValue() == CrystalClear.Always) {
            for (Entity entities : Surround.mc.world.loadedEntityList) {
                if (!(entities instanceof EntityEnderCrystal) || !(Surround.mc.player.getDistance(entities.posX, entities.posY, entities.posZ) < (double)crystalClearRange.getValue().intValue()) || EntityUtil.isSafe(Surround.mc.player)) continue;
                if (rotateCrystal.getValue().booleanValue()) {
                    RotationUtil.faceVectorPacketInstant(new Vec3d(entities.posX, entities.posY, entities.posZ));
                }
                EntityUtil.attackEntity(entities, packetCrystal.getValue(), swingCrystalClear.getValue());
            }
        }
    }

    public void doClearBlocks(BlockPos pos) {
        EnumFacing side;
        if (blockClear.getValue().booleanValue() && Surround.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < (double)blockClearRange.getValue().intValue() && (side = BlockUtil.getFirstFacing(pos)) != null) {
            this.facing = side;
            Surround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            Surround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }

    public void doDynamicExtend(BlockPos pos) {
        if (this.intersectsWithEntity(pos) && Surround.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            BlockPos extend1 = pos.add(1, 0, 0);
            BlockPos extend2 = pos.add(-1, 0, 0);
            BlockPos extend3 = pos.add(0, 0, 1);
            BlockPos extend4 = pos.add(0, 0, -1);
            if (!this.intersectsWithEntity(extend1) && TestUtil.isBlockEmpty(extend1)) {
                this.placeBlock(extend1, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doDynamicExtraExtend(extend1);
            }
            if (!this.intersectsWithEntity(extend2) && TestUtil.isBlockEmpty(extend2)) {
                this.placeBlock(extend2, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doDynamicExtraExtend(extend2);
            }
            if (!this.intersectsWithEntity(extend3) && TestUtil.isBlockEmpty(extend3)) {
                this.placeBlock(extend3, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doDynamicExtraExtend(extend3);
            }
            if (!this.intersectsWithEntity(extend4) && TestUtil.isBlockEmpty(extend4)) {
                this.placeBlock(extend4, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doDynamicExtraExtend(extend4);
            }
        } else {
            return;
        }
    }

    public void doBlockExtend(BlockPos pos) {
        if (Surround.mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL || BlockUtil.getBlockDamage(pos) != 0.0f && antiCity.getValue() == AntiCity.Smart) {
            BlockPos extend1 = pos.add(1, 0, 0);
            BlockPos extend2 = pos.add(-1, 0, 0);
            BlockPos extend3 = pos.add(0, 0, 1);
            BlockPos extend4 = pos.add(0, 0, -1);
            if (TestUtil.isBlockEmpty(extend1)) {
                this.placeBlock(extend1, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (TestUtil.isBlockEmpty(extend2)) {
                this.placeBlock(extend2, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (TestUtil.isBlockEmpty(extend3)) {
                this.placeBlock(extend3, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (TestUtil.isBlockEmpty(extend4)) {
                this.placeBlock(extend4, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
        } else {
            return;
        }
    }

    public void doExtraDynamicExtend(BlockPos pos) {
        if (this.intersectsWithEntity(pos) && Surround.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            BlockPos extend1 = pos.add(1, 0, 0);
            BlockPos extend2 = pos.add(-1, 0, 0);
            BlockPos extend3 = pos.add(0, 0, 1);
            BlockPos extend4 = pos.add(0, 0, -1);
            if (!this.intersectsWithEntity(extend1) && TestUtil.isBlockEmpty(extend1)) {
                this.placeBlock(extend1, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (!this.intersectsWithEntity(extend2) && TestUtil.isBlockEmpty(extend2)) {
                this.placeBlock(extend2, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (!this.intersectsWithEntity(extend3) && TestUtil.isBlockEmpty(extend3)) {
                this.placeBlock(extend3, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
            if (!this.intersectsWithEntity(extend4) && TestUtil.isBlockEmpty(extend4)) {
                this.placeBlock(extend4, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            }
        } else {
            return;
        }
    }

    public void doDynamicExtraExtend(BlockPos pos) {
        if (this.intersectsWithEntity(pos) && Surround.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            BlockPos extend1 = pos.add(1, 0, 0);
            BlockPos extend2 = pos.add(-1, 0, 0);
            BlockPos extend3 = pos.add(0, 0, 1);
            BlockPos extend4 = pos.add(0, 0, -1);
            if (!this.intersectsWithEntity(extend1) && TestUtil.isBlockEmpty(extend1)) {
                this.placeBlock(extend1, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doExtraDynamicExtend(extend1);
            }
            if (!this.intersectsWithEntity(extend2) && TestUtil.isBlockEmpty(extend2)) {
                this.placeBlock(extend2, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doExtraDynamicExtend(extend2);
            }
            if (!this.intersectsWithEntity(extend3) && TestUtil.isBlockEmpty(extend3)) {
                this.placeBlock(extend3, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doExtraDynamicExtend(extend3);
            }
            if (!this.intersectsWithEntity(extend4) && TestUtil.isBlockEmpty(extend4)) {
                this.placeBlock(extend4, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
            } else {
                this.doExtraDynamicExtend(extend4);
            }
        } else {
            return;
        }
    }

    private void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean sneaking) {
        int oldSlot = Surround.mc.player.inventory.currentItem;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        int old_slot = -1;
        BlockPos ecPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
            if (sensitivity.getValue() == Sensitivity.High && obbySlot != Surround.mc.player.inventory.currentItem) {
                old_slot = Surround.mc.player.inventory.currentItem;
                Surround.mc.player.inventory.currentItem = obbySlot;
            }
            if (sensitivity.getValue() == Sensitivity.Low) {
                Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
            }
            if (timer.passedMs(delay.getValue().intValue()) && TestUtil.canPlaceBlock(pos)) {
                BlockUtil.placeBlock(pos, hand, rotate, packet, extraPacket.getValue(), sneaking);
                timer.reset();
            }
            if (sensitivity.getValue() == Sensitivity.High && old_slot != -1) {
                Surround.mc.player.inventory.currentItem = old_slot;
            }
            if (sensitivity.getValue() == Sensitivity.Low) {
                Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            }
        } else if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
            if (sensitivity.getValue() == Sensitivity.High && echestSlot != Surround.mc.player.inventory.currentItem) {
                old_slot = Surround.mc.player.inventory.currentItem;
                Surround.mc.player.inventory.currentItem = echestSlot;
            }
            if (sensitivity.getValue() == Sensitivity.Low) {
                Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
            }
            if (timer.passedMs(delay.getValue().intValue()) && TestUtil.canPlaceBlock(pos)) {
                BlockUtil.placeBlock(pos, hand, rotate, packet, extraPacket.getValue(), sneaking);
                timer.reset();
            }
            if (sensitivity.getValue() == Sensitivity.High && old_slot != -1) {
                Surround.mc.player.inventory.currentItem = old_slot;
            }
            if (sensitivity.getValue() == Sensitivity.Low) {
                Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            }
        }
    }

    private boolean intersectsWithEntity(BlockPos pos) {
        for (Entity entity : Surround.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem || entity instanceof EntityEnderCrystal || !new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    public static enum CrystalClear {
        Always,
        Toggle,
        None;

    }

    public static enum AntiCity {
        Smart,
        None;

    }

    public static enum Sensitivity {
        High,
        Low;

    }
}
