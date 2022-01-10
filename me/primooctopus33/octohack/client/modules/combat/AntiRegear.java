package me.primooctopus33.octohack.client.modules.combat;

import java.util.HashSet;
import java.util.Set;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.DamageUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiRegear
extends Module {
    private final Setting<Float> targetRange = this.register(new Setting<Float>("Target Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    private final Set<BlockPos> shulkerBlackList = new HashSet<BlockPos>();

    public AntiRegear() {
        super("AntiRegear", "Mines regear shulkers", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (DamageUtil.getTarget(this.targetRange.getValue().floatValue()) == null) {
            return;
        }
        if (!AntiRegear.mc.player.onGround) {
            return;
        }
        for (BlockPos pos : BlockUtil.getSphere(5.0f, true)) {
            if (!(AntiRegear.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) || this.shulkerBlackList.contains(pos)) continue;
            AntiRegear.mc.player.swingArm(EnumHand.MAIN_HAND);
            int lastSlot = -1;
            if (AntiRegear.mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
                lastSlot = AntiRegear.mc.player.inventory.currentItem;
                int pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                if (pickSlot != -1) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)));
                }
            }
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
            if (lastSlot == -1) continue;
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (AntiRegear.mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox) {
                this.shulkerBlackList.add(packet.getPos().offset(packet.getDirection()));
            }
        }
    }
}
