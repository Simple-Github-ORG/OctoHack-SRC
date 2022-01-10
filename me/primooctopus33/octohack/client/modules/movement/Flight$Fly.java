package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.util.Util;
import net.minecraft.entity.player.PlayerCapabilities;

class Flight$Fly {
    private Flight$Fly() {
    }

    protected void enable() {
        Util.mc.addScheduledTask(() -> {
            if (Util.mc.player == null || Util.mc.player.capabilities == null) {
                return;
            }
            Util.mc.player.capabilities.allowFlying = true;
            Util.mc.player.capabilities.isFlying = true;
        });
    }

    protected void disable() {
        Util.mc.addScheduledTask(() -> {
            if (Util.mc.player == null || Util.mc.player.capabilities == null) {
                return;
            }
            PlayerCapabilities gmCaps = new PlayerCapabilities();
            Util.mc.playerController.getCurrentGameType().configurePlayerCapabilities(gmCaps);
            PlayerCapabilities capabilities = Util.mc.player.capabilities;
            capabilities.allowFlying = gmCaps.allowFlying;
            capabilities.isFlying = gmCaps.allowFlying && capabilities.isFlying;
            capabilities.setFlySpeed(gmCaps.getFlySpeed());
        });
    }
}
