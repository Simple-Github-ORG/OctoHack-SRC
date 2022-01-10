package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFish
extends Module {
    public boolean cast;
    public boolean nospam = false;
    int oldSlot;
    int rodSlot;
    public Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    public Setting<Boolean> silent = this.register(new Setting<Boolean>("Silent Rod", true));

    public AutoFish() {
        super("AutoFish", "Automatically catches items you get from fishing", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        this.cast = true;
        this.nospam = true;
    }

    @Override
    public void onTick() {
        if (this.cast && this.nospam) {
            if (this.silent.getValue().booleanValue()) {
                this.oldSlot = AutoFish.mc.player.inventory.currentItem;
                this.rodSlot = InventoryUtil.find(Items.FISHING_ROD);
                AutoFish.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.rodSlot));
                AutoFish.mc.playerController.processRightClick(AutoFish.mc.player, AutoFish.mc.world, EnumHand.MAIN_HAND);
                AutoFish.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            } else {
                AutoFish.mc.playerController.processRightClick(AutoFish.mc.player, AutoFish.mc.world, EnumHand.MAIN_HAND);
            }
            if (this.swing.getValue().booleanValue()) {
                AutoFish.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.nospam = false;
        }
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
            AutoFish.mc.playerController.processRightClick(AutoFish.mc.player, AutoFish.mc.world, EnumHand.MAIN_HAND);
            if (this.swing.getValue().booleanValue()) {
                AutoFish.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.cast = true;
            this.nospam = true;
        }
    }
}
