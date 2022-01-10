package me.primooctopus33.octohack.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.HUD;
import me.primooctopus33.octohack.util.Util;

class ModuleManager$Animation
extends Thread {
    public Module module;
    public float offset;
    public float vOffset;
    ScheduledExecutorService service;

    public ModuleManager$Animation() {
        super("Animation");
        this.service = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
            for (Module module : ModuleManager.this.sortedModules) {
                String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                    if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                    module.arrayListOffset -= module.offset;
                    module.sliding = true;
                    continue;
                }
                if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                    module.arrayListOffset += module.offset;
                    module.sliding = true;
                    continue;
                }
                module.sliding = false;
            }
        } else {
            for (String e : ModuleManager.this.sortedModulesABC) {
                Module module = OctoHack.moduleManager.getModuleByName(e);
                String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                    if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                    module.arrayListOffset -= module.offset;
                    module.sliding = true;
                    continue;
                }
                if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                    module.arrayListOffset += module.offset;
                    module.sliding = true;
                    continue;
                }
                module.sliding = false;
            }
        }
    }

    @Override
    public void start() {
        System.out.println("Starting animation thread.");
        this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
    }
}
