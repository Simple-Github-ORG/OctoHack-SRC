package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.math.RayTraceResult;

public enum NoFall$State {
    FALL_CHECK{

        @Override
        public NoFall$State onSend(PacketEvent.Send event) {
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
    ,
    WAIT_FOR_ELYTRA_DEQUIP{

        @Override
        public NoFall$State onReceive(PacketEvent.Receive event) {
            if (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot) {
                return REEQUIP_ELYTRA;
            }
            return this;
        }
    }
    ,
    REEQUIP_ELYTRA{

        @Override
        public NoFall$State onUpdate() {
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, Util.mc.player);
            Util.mc.playerController.updateController();
            int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
            if (slot == -1) {
                Command.sendMessage("\u00a7cElytra not found after regain?");
                return WAIT_FOR_NEXT_REQUIP;
            }
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, Util.mc.player);
            Util.mc.playerController.updateController();
            bypassTimer.reset();
            return RESET_TIME;
        }
    }
    ,
    WAIT_FOR_NEXT_REQUIP{

        @Override
        public NoFall$State onUpdate() {
            if (bypassTimer.passedMs(250L)) {
                return REEQUIP_ELYTRA;
            }
            return this;
        }
    }
    ,
    RESET_TIME{

        @Override
        public NoFall$State onUpdate() {
            if (Util.mc.player.onGround || bypassTimer.passedMs(250L)) {
                Util.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), 1337));
                return FALL_CHECK;
            }
            return this;
        }
    };


    public NoFall$State onSend(PacketEvent.Send e) {
        return this;
    }

    public NoFall$State onReceive(PacketEvent.Receive e) {
        return this;
    }

    public NoFall$State onUpdate() {
        return this;
    }
}
