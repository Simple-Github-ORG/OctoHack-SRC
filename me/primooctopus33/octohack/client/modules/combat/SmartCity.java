package me.primooctopus33.octohack.client.modules.combat;

import java.awt.Color;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SmartCity
extends Module {
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 1, 10));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> oneFifteen = this.register(new Setting<Boolean>("1.15", false));
    public final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    public final Setting<Boolean> crystalCheck = this.register(new Setting<Boolean>("Crystal Check", true));
    public final Setting<Boolean> pickOnly = this.register(new Setting<Boolean>("Pickaxe Check", true));
    public final Setting<Boolean> silentPlace = this.register(new Setting<Boolean>("Silent Place", true));
    public final Setting<Boolean> prePlaceCrystal = this.register(new Setting<Boolean>("PrePlace Crystals", false));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", Boolean.valueOf(true), v -> this.render.getValue()));
    public Setting<Boolean> cSync = this.register(new Setting<Object>("Color Sync", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> red = this.register(new Setting<Integer>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> green = this.register(new Setting<Integer>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", Integer.valueOf(155), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.render.getValue()));
    public BlockPos renderPos = null;
    public EntityPlayer target;
    public Timer timer = new Timer();

    public SmartCity() {
        super("SmartCity", "Automatically mines city blocks of opponents", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (SmartCity.nullCheck()) {
            return;
        }
        this.target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
        if (this.target == null) {
            return;
        }
        Double dist = this.range.getValue().doubleValue();
        Vec3d vec = this.target.getPositionVector();
        if (this.pickOnly.getValue().booleanValue() && !(SmartCity.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
            return;
        }
        if (SmartCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.addVector(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.addVector(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.addVector(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.addVector(0.0, 0.0, -1.0));
            BlockPos targetXCrystal = new BlockPos(vec.addVector(2.0, 0.0, 0.0));
            BlockPos targetXMinusCrystal = new BlockPos(vec.addVector(-2.0, 0.0, 0.0));
            BlockPos targetZCrystal = new BlockPos(vec.addVector(0.0, 0.0, 2.0));
            BlockPos targetZMinusCrystal = new BlockPos(vec.addVector(0.0, 0.0, -2.0));
            if (!this.isPlayerOccupied() && !this.crystalCheck.getValue().booleanValue()) {
                if (SmartCity.isBlockValid(targetX)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                }
                if (!SmartCity.isBlockValid(targetX) && SmartCity.isBlockValid(targetXMinus)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && SmartCity.isBlockValid(targetZ)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && !SmartCity.isBlockValid(targetZ) && SmartCity.isBlockValid(targetZMinus)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && !SmartCity.isBlockValid(targetZ) && !SmartCity.isBlockValid(targetZMinus) || SmartCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
            if (this.crystalCheck.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && SmartCity.isBlockValid(targetX)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    this.renderPos = targetX;
                } else if (this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && SmartCity.isBlockValid(targetXMinus)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    this.renderPos = targetXMinus;
                } else if (this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && SmartCity.isBlockValid(targetZ)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    this.renderPos = targetZ;
                } else if (this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) && SmartCity.isBlockValid(targetZMinus)) {
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    SmartCity.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    this.renderPos = targetZMinus;
                } else {
                    if (SmartCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                        this.renderPos = null;
                        return;
                    }
                    this.renderPos = null;
                    return;
                }
            }
            if (this.prePlaceCrystal.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue())) {
                    BlockUtil.placeCrystalOnBlock(targetXCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue())) {
                    BlockUtil.placeCrystalOnBlock(targetXMinusCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue())) {
                    BlockUtil.placeCrystalOnBlock(targetZCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue())) {
                    BlockUtil.placeCrystalOnBlock(targetZMinusCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) || SmartCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
        }
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (this.pickOnly.getValue().booleanValue() && !(SmartCity.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
            return;
        }
        if (SmartCity.nullCheck()) {
            return;
        }
        this.target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
        if (this.target == null) {
            return;
        }
        Double dist = this.range.getValue().doubleValue();
        Vec3d vec = this.target.getPositionVector();
        if (SmartCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.addVector(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.addVector(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.addVector(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.addVector(0.0, 0.0, -1.0));
            BlockPos targetXCrystal = new BlockPos(vec.addVector(2.0, 0.0, 0.0));
            BlockPos targetXMinusCrystal = new BlockPos(vec.addVector(-2.0, 0.0, 0.0));
            BlockPos targetZCrystal = new BlockPos(vec.addVector(0.0, 0.0, 2.0));
            BlockPos targetZMinusCrystal = new BlockPos(vec.addVector(0.0, 0.0, -2.0));
            if (!this.isPlayerOccupied() && !this.crystalCheck.getValue().booleanValue()) {
                if (SmartCity.isBlockValid(targetX)) {
                    RenderUtil.drawBoxESP(targetX, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!SmartCity.isBlockValid(targetX) && SmartCity.isBlockValid(targetXMinus)) {
                    RenderUtil.drawBoxESP(targetXMinus, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && SmartCity.isBlockValid(targetZ)) {
                    RenderUtil.drawBoxESP(targetZ, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && !SmartCity.isBlockValid(targetZ) && SmartCity.isBlockValid(targetZMinus)) {
                    RenderUtil.drawBoxESP(targetZMinus, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!SmartCity.isBlockValid(targetX) && !SmartCity.isBlockValid(targetXMinus) && !SmartCity.isBlockValid(targetZ) && !SmartCity.isBlockValid(targetZMinus) || SmartCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
            if (this.crystalCheck.getValue().booleanValue() && this.target != null && this.renderPos != null) {
                RenderUtil.drawBoxESP(this.renderPos, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            }
            if (this.prePlaceCrystal.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetXCrystal, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetXMinusCrystal, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetZCrystal, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetZMinusCrystal, this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) || SmartCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
        }
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean oneFifteen) {
        return oneFifteen ? SmartCity.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && SmartCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || SmartCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK : SmartCity.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && SmartCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ())).getBlock() instanceof BlockAir && SmartCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || SmartCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isBlockValid(BlockPos pos) {
        IBlockState blockState = SmartCity.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, SmartCity.mc.world, pos) != -1.0f;
    }

    public boolean isPlayerOccupied() {
        return SmartCity.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && SmartCity.mc.player.isHandActive();
    }
}
