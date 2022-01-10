package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AnvilCev
extends Module {
    public final Setting<Float> range = this.register(new Setting<Float>("Place Range", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(7.0f)));
    public final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks Per Tick", 8, 0, 8));
    public final Setting<Boolean> face = this.register(new Setting<Boolean>("Face", true));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", false));
    private final Vec3d[] normal = new Vec3d[]{new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0)};
    private final Vec3d[] faceb = new Vec3d[]{new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0)};
    private int offsetStep = 0;
    private int yLevel;
    private String lastTickTargetName = "";
    private boolean firstRun = true;

    public AnvilCev() {
        super("AnvilCev", "Attempts to break the opponents surround using anvils", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.yLevel = (int)Math.round(AnvilCev.mc.player.posY);
        this.firstRun = true;
        if (AnvilCev.findAnvilInHotbar() == -1) {
            Command.sendMessage("Unable to find Anvils in your Hotbar! Disabling!");
            this.disable();
        }
    }

    @Override
    public void onUpdate() {
        EntityPlayer closest_target = this.findClosestTarget();
        if (closest_target == null) {
            this.disable();
            return;
        }
        if ((int)Math.round(AnvilCev.mc.player.posY) != this.yLevel) {
            this.disable();
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            this.lastTickTargetName = closest_target.getName();
        } else if (!this.lastTickTargetName.equals(closest_target.getName())) {
            this.lastTickTargetName = closest_target.getName();
            this.offsetStep = 0;
        }
        ArrayList place_targets = new ArrayList();
        if (this.face.getValue().booleanValue()) {
            Collections.addAll(place_targets, this.faceb);
        } else {
            Collections.addAll(place_targets, this.normal);
        }
        int blocks_placed = 0;
        while (blocks_placed < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= place_targets.size()) {
                this.offsetStep = 0;
                break;
            }
            BlockPos offset_pos = new BlockPos((Vec3d)place_targets.get(this.offsetStep));
            BlockPos target_pos = new BlockPos(closest_target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = true;
            if (!AnvilCev.mc.world.getBlockState(target_pos).getMaterial().isReplaceable()) {
                should_try_place = false;
            }
            for (Entity entity : AnvilCev.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                should_try_place = false;
                break;
            }
            if (should_try_place && BlockUtil.placeBlock(target_pos, AnvilCev.findAnvilInHotbar(), (boolean)this.rotate.getValue(), (boolean)this.rotate.getValue(), (boolean)this.swing.getValue())) {
                ++blocks_placed;
            }
            ++this.offsetStep;
        }
    }

    public static int findAnvilInHotbar() {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = AnvilCev.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)((Object)stack.getItem())).getBlock()) instanceof BlockAnvil)) continue;
            return i;
        }
        return -1;
    }

    public EntityPlayer findClosestTarget() {
        if (AnvilCev.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : AnvilCev.mc.world.playerEntities) {
            if (target == AnvilCev.mc.player || !target.isEntityAlive() || OctoHack.friendManager.isFriend(target.getName()) || target.getHealth() <= 0.0f || closestTarget != null && AnvilCev.mc.player.getDistance(target) > AnvilCev.mc.player.getDistance(closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }
}
