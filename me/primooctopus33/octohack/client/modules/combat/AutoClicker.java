package me.primooctopus33.octohack.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.concurrent.ThreadLocalRandom;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

public class AutoClicker
extends Module {
    public final Setting<Boolean> weaponOnly = this.register(new Setting<Boolean>("Weapon Only", true));
    public final Setting<Integer> minCPS = this.register(new Setting<Integer>("Miniumum Delay", 10, 0, 250));
    public final Setting<Integer> maxCPS = this.register(new Setting<Integer>("Maxiumum Delay", 15, 0, 250));
    public final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    public final Setting<Boolean> cpsInfo = this.register(new Setting<Boolean>("CPS Info", true));
    public final Setting<Boolean> onlyOverEntity = this.register(new Setting<Boolean>("Only Over Entity", false));
    public Timer timer = new Timer();

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you when certain requirements are met, May get you banned", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (AutoClicker.mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (this.weaponOnly.getValue().booleanValue() && !(AutoClicker.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) && !(AutoClicker.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAxe)) {
                return;
            }
            if (this.timer.passedMs(ThreadLocalRandom.current().nextInt(this.minCPS.getValue(), this.maxCPS.getValue() + 1))) {
                if (this.onlyOverEntity.getValue().booleanValue() && AutoClicker.mc.objectMouseOver.entityHit == null) {
                    return;
                }
                if (this.swing.getValue().booleanValue()) {
                    AutoClicker.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                try {
                    AutoClicker.mc.playerController.attackEntity(AutoClicker.mc.player, AutoClicker.mc.objectMouseOver.entityHit);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (this.cpsInfo.getValue().booleanValue()) {
                    Command.sendMessage("You are Clicking " + ChatFormatting.AQUA + ThreadLocalRandom.current().nextInt(this.minCPS.getValue(), this.maxCPS.getValue() + 1) + " CPS!");
                }
                this.timer.reset();
            }
        }
        if (this.minCPS.getValue() >= this.maxCPS.getValue()) {
            Command.sendMessage("You cannot set your Min CPS higher than your Max CPS!");
            this.disable();
        }
    }
}
