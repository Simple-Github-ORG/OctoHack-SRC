package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.NoFall;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;

final class NoFall$State$1
extends NoFall.State {
    @Override
    public NoFall.State onSend(PacketEvent.Send event) {
        RayTraceResult result = Util.mc.world.rayTraceBlocks(Util.mc.player.getPositionVector(), Util.mc.player.getPositionVector().addVector(0.0, -3.0, 0.0), true, true, false);
        if (event.getPacket() instanceof CPacketPlayer && Util.mc.player.fallDistance >= 3.0f && result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
            if (slot != -1) {
                Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, Util.mc.player);
                ogslot = slot;
                Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                return WAIT_FOR_ELYTRA_DEQUIP;
            }
            return this;
        }
        return this;
    }
}
