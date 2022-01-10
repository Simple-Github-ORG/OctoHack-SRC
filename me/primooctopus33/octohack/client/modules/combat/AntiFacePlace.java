package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AntiFacePlace
extends Module {
    public final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks Per Tick", 16, 1, 16));
    public final Setting<Boolean> smartEnable = this.register(new Setting<Boolean>("Smart Enable", true));
    public final Setting<Boolean> disables = this.register(new Setting<Boolean>("Disables", true));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public final Setting<Boolean> disableOnJump = this.register(new Setting<Boolean>("Disable On Jump", true));
    public final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", false));
    private int offsetStep = 0;
    private final Vec3d[] offsetsDefault = new Vec3d[]{new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(-1.0, 3.0, 0.0)};

    public AntiFacePlace() {
        super("AntiFacePlace", "Attempts to stop opponents from faceplacing you", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        EntityPlayerSP player = AntiFacePlace.mc.player;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int oldSlot = AntiFacePlace.mc.player.inventory.currentItem;
        ArrayList place_targets = new ArrayList();
        Collections.addAll(place_targets, this.offsetsDefault);
        int blocks_placed = 0;
        while (blocks_placed < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= place_targets.size()) {
                this.offsetStep = 0;
                break;
            }
            boolean placing = true;
            BlockPos offsetPos = new BlockPos((Vec3d)place_targets.get(this.offsetStep));
            BlockPos placePos = new BlockPos(((EntityPlayer)Objects.requireNonNull(player)).getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean should_try_place = AntiFacePlace.mc.world.getBlockState(placePos).getMaterial().isReplaceable();
            for (Entity entity : AntiFacePlace.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                should_try_place = false;
                break;
            }
            if (should_try_place) {
                if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    if (this.smartEnable.getValue().booleanValue()) {
                        if (EntityUtil.isInHole(player)) {
                            AntiFacePlace.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                            BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.rotate.getValue(), false, true);
                            AntiFacePlace.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                            ++blocks_placed;
                        }
                    } else {
                        AntiFacePlace.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                        BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.rotate.getValue(), false, true);
                        AntiFacePlace.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                        ++blocks_placed;
                    }
                } else {
                    Command.sendMessage("You have no obsidian in your hotbar! Toggling!");
                    this.disable();
                }
            }
            ++this.offsetStep;
        }
        if (this.disables.getValue().booleanValue()) {
            this.disable();
        }
        if (this.disableOnJump.getValue().booleanValue() && AntiFacePlace.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.disable();
        }
    }
}
