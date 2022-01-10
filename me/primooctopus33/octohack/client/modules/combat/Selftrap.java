package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import me.primooctopus33.octohack.OctoHack;
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

public class Selftrap
extends Module {
    public final Setting<Boolean> disables = this.register(new Setting<Boolean>("Disables", true));
    public final Setting<Boolean> holeToggle = this.register(new Setting<Boolean>("Smart Enable", false));
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Normal));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet Place", true));
    public final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks Per Tick", 16, 1, 16));
    public final Setting<Boolean> disableOnJump = this.register(new Setting<Boolean>("Disable On Jump", true));
    public final Setting<Boolean> disableOnStep = this.register(new Setting<Boolean>("Disable On Step", true));
    private int step = 0;
    private final Vec3d[] simple = new Vec3d[]{new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
    private final Vec3d[] full = new Vec3d[]{new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(0.0, 4.0, 0.0)};

    public Selftrap() {
        super("Selftrap", "Automatically traps yourself so your enemies cant get in your hole", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        EntityPlayerSP player = Selftrap.mc.player;
        EntityPlayer closestTarget = this.findClosestTarget();
        int obsidianSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        ArrayList playerPos = new ArrayList();
        if (this.mode.getValue() == Mode.Normal) {
            Collections.addAll(playerPos, this.simple);
        }
        if (this.mode.getValue() == Mode.Full) {
            Collections.addAll(playerPos, this.full);
        }
        int placedBlocks = 0;
        while (placedBlocks < this.blocksPerTick.getValue()) {
            if (this.step >= playerPos.size()) {
                this.step = 0;
                break;
            }
            boolean placing = true;
            BlockPos offsetPos = new BlockPos((Vec3d)playerPos.get(this.step));
            BlockPos placePos = new BlockPos(((EntityPlayer)Objects.requireNonNull(player)).getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean should_try_place = Selftrap.mc.world.getBlockState(placePos).getMaterial().isReplaceable();
            for (Entity entity : Selftrap.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                should_try_place = false;
                break;
            }
            if (should_try_place) {
                int oldSlot = Selftrap.mc.player.inventory.currentItem;
                if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    if (this.holeToggle.getValue().booleanValue()) {
                        if (EntityUtil.isInHole(player)) {
                            Selftrap.mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianSlot));
                            BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, true);
                            Selftrap.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                            ++placedBlocks;
                        }
                    } else {
                        Selftrap.mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianSlot));
                        BlockUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, true);
                        Selftrap.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    }
                    ++placedBlocks;
                } else {
                    Command.sendMessage("You have no obsidian in your hotbar! Toggling!");
                    this.disable();
                }
            }
            ++this.step;
        }
        if (this.disables.getValue().booleanValue()) {
            this.disable();
        }
        if (this.disableOnJump.getValue().booleanValue() && Selftrap.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.disable();
        }
        if (this.disableOnStep.getValue().booleanValue() && OctoHack.moduleManager.isModuleEnabled("Step")) {
            this.disable();
        }
    }

    public EntityPlayer findClosestTarget() {
        if (Selftrap.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : Selftrap.mc.world.playerEntities) {
            if (target == Selftrap.mc.player || !target.isEntityAlive() || OctoHack.friendManager.isFriend(target.getName()) || target.getHealth() <= 0.0f || closestTarget != null && Selftrap.mc.player.getDistance(target) > Selftrap.mc.player.getDistance(closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    public static enum Mode {
        Normal,
        Full;

    }
}
