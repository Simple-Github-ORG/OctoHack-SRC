package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Bind;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class SilentAutoXP
extends Module {
    int oldSlot;
    int xpSlot;
    int oldRotations;
    public final Setting<Boolean> rotatetofeet = this.register(new Setting<Boolean>("Rotate to Feet", true));
    public final Setting<Bind> xpBind = this.register(new Setting<Bind>("Bind", new Bind(-1)));

    public SilentAutoXP() {
        super("SilentAutoXP", "Switches to Experience Bottles and Throws them at your feet to mend your armor", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.oldRotations = (int)SilentAutoXP.mc.player.rotationPitch;
        this.oldSlot = SilentAutoXP.mc.player.inventory.currentItem;
    }

    @Override
    public void onUpdate() {
        if (this.xpBind.getValue().isDown()) {
            this.useExp();
        }
    }

    public void useExp() {
        this.oldRotations = (int)SilentAutoXP.mc.player.rotationPitch;
        if (InventoryUtil.find(Items.EXPERIENCE_BOTTLE) >= 0) {
            this.oldSlot = SilentAutoXP.mc.player.inventory.currentItem;
            this.xpSlot = InventoryUtil.find(Items.EXPERIENCE_BOTTLE);
            SilentAutoXP.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.xpSlot));
            if (this.rotatetofeet.getValue().booleanValue()) {
                SilentAutoXP.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(SilentAutoXP.mc.player.cameraYaw, 90.0f, SilentAutoXP.mc.player.onGround));
            }
            SilentAutoXP.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            SilentAutoXP.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            if (this.rotatetofeet.getValue().booleanValue()) {
                SilentAutoXP.mc.player.rotationPitch = this.oldRotations;
            }
        }
    }
}
