package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.NoFall;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

final class NoFall$State$5
extends NoFall.State {
    @Override
    public NoFall.State onUpdate() {
        if (Util.mc.player.onGround || bypassTimer.passedMs(250L)) {
            Util.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), 1337));
            return FALL_CHECK;
        }
        return this;
    }
}
