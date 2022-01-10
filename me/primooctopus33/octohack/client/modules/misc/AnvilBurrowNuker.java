package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

public class AnvilBurrowNuker
extends Module {
    private final Setting<Float> range = this.register(new Setting<Float>("Break Range", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    private final Setting<Boolean> silent = this.register(new Setting<Boolean>("Silent", true));
    private final Timer timer = new Timer();

    public AnvilBurrowNuker() {
        super("AnvilBurrowNuker", "Mines anvil burrow", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onUpdate() {
        BlockPos blockPos;
        if (!AnvilBurrowNuker.mc.player.onGround) {
            return;
        }
        EntityPlayer target = EntityUtil.getClosestEnemy(this.range.getValue().floatValue());
        if (target != null && AnvilBurrowNuker.mc.world.getBlockState(blockPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY + 0.2), Math.floor(target.posZ))).getBlock() == Blocks.ANVIL && blockPos.distanceSq(target.posX, target.posY, target.posZ) <= (double)this.range.getValue().floatValue()) {
            int pickSlot;
            AnvilBurrowNuker.mc.player.swingArm(EnumHand.MAIN_HAND);
            int oldSlot = AnvilBurrowNuker.mc.player.inventory.currentItem;
            if (AnvilBurrowNuker.mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE && this.silent.getValue().booleanValue() && (pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)) != -1) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)));
            }
            this.breakBlock();
        }
    }

    public void breakBlock() {
        EntityPlayer target = EntityUtil.getClosestEnemy(this.range.getValue().floatValue());
        BlockPos blockPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY + 0.2), Math.floor(target.posZ));
        if (this.rotate.getValue().booleanValue()) {
            float[] r = MathUtil.calculateLookAt(target.posX + 0.5, target.posY + 0.5, target.posZ + 0.5, AnvilBurrowNuker.mc.player);
            OctoHack.rotationManager.yaw = r[0];
            OctoHack.rotationManager.pitch = r[1];
        }
        AnvilBurrowNuker.mc.player.swingArm(EnumHand.MAIN_HAND);
        if (this.packet.getValue().booleanValue()) {
            AnvilBurrowNuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
            AnvilBurrowNuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
        } else if (mc.getConnection().getPlayerInfo(AnvilBurrowNuker.mc.player.getUniqueID()).getGameType() == GameType.CREATIVE) {
            AnvilBurrowNuker.mc.playerController.clickBlock(blockPos, EnumFacing.DOWN);
        } else {
            AnvilBurrowNuker.mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.DOWN);
        }
    }
}
