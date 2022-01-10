package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoBowRelease
extends Module {
    public final Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("AutoSwitch", true));
    public final Setting<Boolean> autoFire = this.register(new Setting<Boolean>("AutoFire", true));
    public final Setting<Integer> tickDelay = this.register(new Setting<Integer>("TickDelay", 3, 0, 8));
    public final Setting<Boolean> bowBomb = this.register(new Setting<Boolean>("BowBomb", false));
    public boolean readyToFire = true;

    public AutoBowRelease() {
        super("AutoBowRelease", "Automatically spams your bow", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onUpdate() {
        int bowSlot = InventoryUtil.find(Items.BOW);
        if (this.autoSwitch.getValue().booleanValue()) {
            InventoryUtil.switchToHotbarSlot(bowSlot, false);
        }
        if (AutoBowRelease.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow) {
            if (this.autoFire.getValue().booleanValue()) {
                this.readyToFire = true;
                if (true) {
                    AutoBowRelease.mc.playerController.processRightClick(AutoBowRelease.mc.player, AutoBowRelease.mc.world, EnumHand.MAIN_HAND);
                    this.readyToFire = false;
                }
                if (AutoBowRelease.mc.player.getItemInUseMaxCount() >= this.tickDelay.getValue()) {
                    AutoBowRelease.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, AutoBowRelease.mc.player.getHorizontalFacing()));
                    AutoBowRelease.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(AutoBowRelease.mc.player.getActiveHand()));
                    AutoBowRelease.mc.player.stopActiveHand();
                    this.readyToFire = true;
                }
            }
            if (AutoBowRelease.mc.player.getItemInUseMaxCount() >= this.tickDelay.getValue()) {
                AutoBowRelease.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, AutoBowRelease.mc.player.getHorizontalFacing()));
                AutoBowRelease.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(AutoBowRelease.mc.player.getActiveHand()));
                AutoBowRelease.mc.player.stopActiveHand();
            }
        }
    }
}
