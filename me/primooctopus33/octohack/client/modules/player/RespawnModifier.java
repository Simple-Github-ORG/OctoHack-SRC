package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RespawnModifier
extends Module {
    public Setting<Boolean> antiDeathScreen = this.register(new Setting<Boolean>("AntiDeathScreen", false));
    public Setting<Boolean> deathCoords = this.register(new Setting<Boolean>("DeathCoords", true));
    public Setting<Boolean> respawn = this.register(new Setting<Boolean>("AutoRespawn", false));

    public RespawnModifier() {
        super("RespawnModifier", "Automatically clicks the respawn button if you die", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onDisplayDeathScreen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            if (this.deathCoords.getValue().booleanValue() && event.getGui() instanceof GuiGameOver) {
                Command.sendMessage(String.format("You died at x %d y %d z %d", (int)RespawnModifier.mc.player.posX, (int)RespawnModifier.mc.player.posY, (int)RespawnModifier.mc.player.posZ));
            }
            if (this.respawn.getValue() != false && RespawnModifier.mc.player.getHealth() <= 0.0f || this.antiDeathScreen.getValue().booleanValue() && RespawnModifier.mc.player.getHealth() > 0.0f) {
                event.setCanceled(true);
                RespawnModifier.mc.player.respawnPlayer();
            }
        }
    }
}
