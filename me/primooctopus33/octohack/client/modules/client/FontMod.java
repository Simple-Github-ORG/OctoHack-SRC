package me.primooctopus33.octohack.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.GraphicsEnvironment;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.ClientEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontMod
extends Module {
    private static FontMod INSTANCE = new FontMod();
    public Setting<String> fontName = this.register(new Setting<String>("Font Name", "Arial", "Name of the font."));
    public Setting<Boolean> antiAlias = this.register(new Setting<Boolean>("Anti Alias", Boolean.valueOf(true), "Smoother font."));
    public Setting<Boolean> fractionalMetrics = this.register(new Setting<Boolean>("Metrics", Boolean.valueOf(true), "Thinner font."));
    public Setting<Integer> fontSize = this.register(new Setting<Integer>("Size", Integer.valueOf(18), Integer.valueOf(12), Integer.valueOf(30), "Size of the font."));
    public Setting<Integer> fontStyle = this.register(new Setting<Integer>("Style", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(3), "Style of the font."));
    private boolean reloadFont = false;

    public FontMod() {
        super("CustomFont", "Allows you to change your client font", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static FontMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontMod();
        }
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        String[] fonts;
        for (String s : fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font)) {
                return true;
            }
            if (!message) continue;
            Command.sendMessage(s);
        }
        return false;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !FontMod.checkFont(setting.getPlannedValue().toString(), false)) {
                Command.sendMessage(ChatFormatting.RED + "That font doesnt exist.");
                event.setCanceled(true);
                return;
            }
            this.reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (this.reloadFont) {
            OctoHack.textManager.init(false);
            this.reloadFont = false;
        }
    }
}
