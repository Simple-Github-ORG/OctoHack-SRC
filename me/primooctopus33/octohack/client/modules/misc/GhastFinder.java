package me.primooctopus33.octohack.client.modules.misc;

import java.util.HashSet;
import java.util.Set;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;

public class GhastFinder
extends Module {
    public Setting<Boolean> Chat = this.register(new Setting<Boolean>("Chat", true));
    public Setting<Boolean> Sound = this.register(new Setting<Boolean>("Sound", true));
    public Setting<Boolean> glowing = this.register(new Setting<Boolean>("Glow Ghast", true));
    private final Set<Entity> ghasts = new HashSet<Entity>();

    public GhastFinder() {
        super("GhastFinder", "Alerts you when a ghast spawns", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        this.ghasts.clear();
    }

    @Override
    public void onUpdate() {
        for (Entity entity : GhastFinder.mc.world.getLoadedEntityList()) {
            if (!(entity instanceof EntityGhast) || this.ghasts.contains(entity)) continue;
            if (this.Chat.getValue().booleanValue()) {
                Command.sendMessage("Ghast Detected at: " + entity.getPosition().getX() + "x, " + entity.getPosition().getY() + "y, " + entity.getPosition().getZ() + "z.");
            }
            this.ghasts.add(entity);
            if (!this.Sound.getValue().booleanValue()) continue;
            GhastFinder.mc.player.playSound(SoundEvents.ENTITY_GHAST_AMBIENT, 1.0f, 1.0f);
            if (!this.glowing.getValue().booleanValue()) continue;
            entity.setGlowing(true);
        }
    }
}
