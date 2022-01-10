package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Timer;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiSurround
extends Module {
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 6, 0, 20));
    public final Setting<Sensitivity> sensitivity = this.register(new Setting<Sensitivity>("Sensitivity", Sensitivity.High));
    public final Setting<Integer> breakDelay = this.register(new Setting<Integer>("Break Delay", 10, 0, 150));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public final Setting<Boolean> extraPacket = this.register(new Setting<Boolean>("Extra Packet", true));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> bypass = this.register(new Setting<Boolean>("Bypass", false));
    public final Setting<Boolean> oneFifteen = this.register(new Setting<Boolean>("1.15", false));
    public Timer timer = new Timer();
    public EntityPlayer target;

    public AntiSurround() {
        super("AntiSurround", "Uses anvils and crystals to bypass enemy surround", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (AntiSurround.nullCheck()) {
            return;
        }
        this.target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
        if (this.target == null) {
            return;
        }
        Double dist = this.range.getValue().doubleValue();
        Vec3d vec = this.target.getPositionVector();
        if (this.fullItemNullCheck()) {
            return;
        }
        if (AntiSurround.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.addVector(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.addVector(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.addVector(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.addVector(0.0, 0.0, -1.0));
            BlockPos targetXCrystal = new BlockPos(vec.addVector(2.0, 0.0, 0.0));
            BlockPos targetXMinusCrystal = new BlockPos(vec.addVector(-2.0, 0.0, 0.0));
            BlockPos targetZCrystal = new BlockPos(vec.addVector(0.0, 0.0, 2.0));
            BlockPos targetZMinusCrystal = new BlockPos(vec.addVector(0.0, 0.0, -2.0));
            if (this.target != null) {
                if (this.isBlockValid(targetX) && this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue())) {
                    float breakTime = AntiSurround.mc.world.getBlockState(targetX).getBlockHardness(AntiSurround.mc.world, targetX);
                    if (this.timer.passedMs((long)breakTime) && !WorldUtils.empty.contains(targetX)) {
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    }
                    if (this.canPlaceBlock(targetX) && this.timer.passedMs(this.breakDelay.getValue().intValue())) {
                        this.placeBlock(targetX, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        if (this.bypass.getValue().booleanValue()) {
                            this.placeBlock(new BlockPos(targetX.getX(), targetX.getY() + 1, targetX.getZ()), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        }
                        this.timer.reset();
                    }
                } else if (this.isBlockValid(targetXMinus) && this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue())) {
                    float breakTime = AntiSurround.mc.world.getBlockState(targetXMinus).getBlockHardness(AntiSurround.mc.world, targetXMinus);
                    if (this.timer.passedMs((long)breakTime) && !WorldUtils.empty.contains(targetXMinus)) {
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    }
                    if (this.canPlaceBlock(targetXMinus) && this.timer.passedMs(this.breakDelay.getValue().intValue())) {
                        this.placeBlock(targetXMinus, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        if (this.bypass.getValue().booleanValue()) {
                            this.placeBlock(new BlockPos(targetXMinus.getX(), targetXMinus.getY() + 1, targetXMinus.getZ()), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        }
                        this.timer.reset();
                    }
                } else if (this.isBlockValid(targetZ) && this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue())) {
                    float breakTime = AntiSurround.mc.world.getBlockState(targetZMinus).getBlockHardness(AntiSurround.mc.world, targetZMinus);
                    if (this.timer.passedMs((long)breakTime) && !WorldUtils.empty.contains(targetZ)) {
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    }
                    if (this.canPlaceBlock(targetZ) && this.timer.passedMs(this.breakDelay.getValue().intValue())) {
                        this.placeBlock(targetZ, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        if (this.bypass.getValue().booleanValue()) {
                            this.placeBlock(new BlockPos(targetZ.getX(), targetZ.getY() + 1, targetZ.getZ()), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        }
                        this.timer.reset();
                    }
                } else if (this.isBlockValid(targetZMinus) && this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue())) {
                    float breakTime = AntiSurround.mc.world.getBlockState(targetZMinus).getBlockHardness(AntiSurround.mc.world, targetZMinus);
                    if (this.timer.passedMs((long)breakTime) && !WorldUtils.empty.contains(targetZMinusCrystal)) {
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                        AntiSurround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    }
                    if (this.canPlaceBlock(targetZMinus) && this.timer.passedMs(this.breakDelay.getValue().intValue())) {
                        this.placeBlock(targetZMinus, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        if (this.bypass.getValue().booleanValue()) {
                            this.placeBlock(new BlockPos(targetZMinus.getX(), targetZMinus.getY() + 1, targetZMinus.getZ()), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.extraPacket.getValue(), true);
                        }
                        this.timer.reset();
                    }
                }
                if (!((this.isBlockValid(targetX) || this.isBlockValid(targetXMinus) || this.isBlockValid(targetZ) || this.isBlockValid(targetZMinus)) && this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue() != false && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) || AntiSurround.mc.player.getPositionVector().distanceTo(vec) > dist))) {
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof SPacketOpenWindow) {
            event.setCanceled(true);
        }
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean oneFifteen) {
        return oneFifteen ? AntiSurround.mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR && AntiSurround.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || AntiSurround.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK : AntiSurround.mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR && AntiSurround.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ())).getBlock() == Blocks.AIR && AntiSurround.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || AntiSurround.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK;
    }

    public boolean isBlockValid(BlockPos pos) {
        return AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.PORTAL || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.END_PORTAL || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.END_PORTAL_FRAME || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.END_GATEWAY || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.BARRIER || AntiSurround.mc.world.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    public boolean fullItemNullCheck() {
        return InventoryUtil.findHotbarBlock(BlockAnvil.class) == -1 || InventoryUtil.findHotbarBlock(ItemPickaxe.class) == -1 || InventoryUtil.find(ItemEndCrystal.class) == -1;
    }

    public boolean canPlaceBlock(BlockPos pos) {
        return AntiSurround.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    public void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean extraPacket, boolean sneaking) {
        int anvilSlot = InventoryUtil.findHotbarBlock(BlockAnvil.class);
        int oldSlot = AntiSurround.mc.player.inventory.currentItem;
        if (this.sensitivity.getValue() == Sensitivity.High) {
            AntiSurround.mc.player.inventory.currentItem = anvilSlot;
            BlockUtil.placeBlock(pos, hand, rotate, packet, extraPacket, sneaking);
            AntiSurround.mc.player.inventory.currentItem = oldSlot;
        }
        if (this.sensitivity.getValue() == Sensitivity.Low) {
            AntiSurround.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
            BlockUtil.placeBlock(pos, hand, rotate, packet, extraPacket, sneaking);
            AntiSurround.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
    }

    public static double getEntitySpeed(Entity entity) {
        if (entity != null) {
            double distTraveledLastTickX = entity.posX - entity.prevPosX;
            double distTraveledLastTickZ = entity.posZ - entity.prevPosZ;
            double speed = MathHelper.sqrt((double)(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ));
            return speed * 20.0;
        }
        return 0.0;
    }

    public static enum Sensitivity {
        High,
        Low;

    }
}
