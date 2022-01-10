package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Bind;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoPot
extends Module {
    int potSlot;
    int oldSlot;
    boolean spoofRotate;
    private final Timer timer = new Timer();
    public final Setting<Float> health = this.register(new Setting<Float>("Health", Float.valueOf(16.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 250));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate to Feet", true));
    public final Setting<Boolean> disable = this.register(new Setting<Boolean>("Disable No Pots", true));
    public final Setting<Bind> forcePot = this.register(new Setting<Bind>("ForcePot", new Bind(-1)));

    public AutoPot() {
        super("AutoPot", "Automatically throws a potion when requirements are met", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        this.potSlot = InventoryUtil.find(Items.SPLASH_POTION);
        this.oldSlot = AutoPot.mc.player.inventory.currentItem;
        if (!AutoPot.nullCheck() && AutoPot.mc.player.getHealth() <= this.health.getValue().floatValue()) {
            this.spoofRotate = true;
            this.throwPot();
            return;
        }
        if (this.disable.getValue().booleanValue() && InventoryUtil.find(Items.SPLASH_POTION) == -1) {
            this.disable();
            Command.sendMessage("No Potions Found, Disabling!");
        }
    }

    public void throwPot() {
        if (InventoryUtil.find(Items.SPLASH_POTION) >= 0 && this.timer.passedMs(this.delay.getValue().longValue())) {
            AutoPot.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.potSlot));
            if (this.rotate.getValue().booleanValue()) {
                AutoPot.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(AutoPot.mc.player.cameraYaw, 90.0f, AutoPot.mc.player.onGround));
            }
            AutoPot.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            AutoPot.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && this.forcePot.getValue().getKey() == Keyboard.getEventKey()) {
            this.spoofRotate = true;
            this.throwPot();
        }
    }

    @Override
    public void onEnable() {
        this.spoofRotate = false;
        AutoPot.mc.player.rotationPitch = (float)((double)AutoPot.mc.player.rotationPitch + 4.0E-4);
        this.oldSlot = AutoPot.mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        AutoPot.mc.player.inventory.currentItem = this.oldSlot;
        AutoPot.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
    }
}
