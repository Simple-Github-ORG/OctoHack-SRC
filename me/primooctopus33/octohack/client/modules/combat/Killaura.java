package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.DamageUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura
extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f)));
    public Setting<Boolean> delay = this.register(new Setting<Boolean>("HitDelay", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public Setting<Boolean> onlySharp = this.register(new Setting<Boolean>("SwordOnly", true));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> neutral = this.register(new Setting<Boolean>("Neutral", false));
    public Setting<Boolean> friends = this.register(new Setting<Boolean>("Friends", false));
    public Setting<Boolean> hostile = this.register(new Setting<Boolean>("Hostile", false));
    public Setting<Boolean> passives = this.register(new Setting<Boolean>("Passives", false));
    public Setting<Boolean> tps = this.register(new Setting<Boolean>("TpsSync", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));

    public Killaura() {
        super("Killaura", "Automatically attacks players near you", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (!this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    private void doKillaura() {
        int wait;
        if (this.onlySharp.getValue().booleanValue() && !EntityUtil.holdingWeapon(Killaura.mc.player)) {
            target = null;
            return;
        }
        int n = this.delay.getValue() == false ? 0 : (wait = (int)((float)DamageUtil.getCooldownByWeapon(Killaura.mc.player) * (this.tps.getValue() != false ? OctoHack.serverManager.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait)) {
            return;
        }
        EntityLivingBase target = EntityUtil.getTarget(this.players.getValue(), this.neutral.getValue(), this.friends.getValue(), this.hostile.getValue(), this.passives.getValue(), 10.0, EntityUtil.toMode("Closest"));
        if (target == null) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            OctoHack.rotationManager.lookAtEntity(target);
        }
        EntityUtil.attackEntity(target, this.packet.getValue(), true);
        this.timer.reset();
    }

    @Override
    public String getDisplayInfo() {
        if (target instanceof EntityPlayer) {
            return target.getName();
        }
        return null;
    }
}
