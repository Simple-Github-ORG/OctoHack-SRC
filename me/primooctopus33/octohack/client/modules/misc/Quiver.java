package me.primooctopus33.octohack.client.modules.misc;

import java.util.List;
import java.util.Objects;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionUtils;

public class Quiver
extends Module {
    private final Setting<Integer> tickDelay = this.register(new Setting<Integer>("TickDelay", 3, 0, 8));

    public Quiver() {
        super("Quiver", "Rotates and shoots yourself with good potion effects", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (Quiver.mc.player != null) {
            List<Integer> arrowSlots;
            if (Quiver.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && Quiver.mc.player.isHandActive() && Quiver.mc.player.getItemInUseMaxCount() >= this.tickDelay.getValue()) {
                Quiver.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(Quiver.mc.player.cameraYaw, -90.0f, Quiver.mc.player.onGround));
                Quiver.mc.playerController.onStoppedUsingItem(Quiver.mc.player);
            }
            if ((arrowSlots = InventoryUtil.getItemInventory(Items.TIPPED_ARROW)).get(0) == -1) {
                return;
            }
            int speedSlot = -1;
            int strengthSlot = -1;
            for (Integer slot : arrowSlots) {
                if (PotionUtils.getPotionFromItem((ItemStack)Quiver.mc.player.inventory.getStackInSlot(slot)).getRegistryName().getResourcePath().contains("swiftness")) {
                    speedSlot = slot;
                    continue;
                }
                if (!Objects.requireNonNull(PotionUtils.getPotionFromItem((ItemStack)Quiver.mc.player.inventory.getStackInSlot(slot)).getRegistryName()).getResourcePath().contains("strength")) continue;
                strengthSlot = slot;
            }
        }
    }

    @Override
    public void onEnable() {
    }

    private int findBow() {
        return InventoryUtil.getItemHotbar(Items.BOW);
    }
}
