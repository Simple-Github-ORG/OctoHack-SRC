package me.primooctopus33.octohack.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.gui.OctoHackGui;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.ClientEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui
extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    public Setting<Boolean> customFov = this.register(new Setting<Boolean>("Custom Fov", false));
    public Setting<Boolean> snowing = this.register(new Setting<Boolean>("Snowing", false));
    public Setting<Boolean> moduleOutline = this.register(new Setting<Boolean>("Module Outline", true));
    public Setting<Boolean> cross = this.register(new Setting<Boolean>("Module Indicator", true));
    public Setting<Boolean> particles = this.register(new Setting<Boolean>("Particles", true));
    public Setting<Integer> particlered = this.register(new Setting<Integer>("Particle Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.particles.getValue()));
    public Setting<Integer> particlegreen = this.register(new Setting<Integer>("Particle Green", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255), v -> this.particles.getValue()));
    public Setting<Integer> particleblue = this.register(new Setting<Integer>("Particle Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.particles.getValue()));
    public Setting<Integer> particleSize = this.register(new Setting<Integer>("Particle Size", 4, 0, 25));
    public Setting<Integer> particleLength = this.register(new Setting<Integer>("Particle Length", Integer.valueOf(203), Integer.valueOf(0), Integer.valueOf(300), v -> this.particles.getValue()));
    public Setting<Integer> safetyCheck = this.register(new Setting<Integer>("Safety Check", 50, 1, 150));
    public Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    public Setting<Boolean> safety = this.register(new Setting<Boolean>("Safety Player", false));
    public Setting<Float> fov = this.register(new Setting<Float>("Fov", Float.valueOf(160.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)));
    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 30, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 167, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("Alpha", 191, 0, 255));
    public Setting<Integer> topRed = this.register(new Setting<Integer>("Second Red", 30, 0, 255));
    public Setting<Integer> topGreen = this.register(new Setting<Integer>("Second Green", 167, 0, 255));
    public Setting<Integer> topBlue = this.register(new Setting<Integer>("Second Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("Hover Alpha", 210, 0, 255));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("H Rainbow Mode", (Object)rainbowMode.Sideway, v -> this.rainbow.getValue()));
    public Setting<rainbowModeArray> rainbowModeA = this.register(new Setting<Object>("A Rainbow Mode", (Object)rainbowModeArray.Static, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue()));
    public float hue;
    private OctoHackGui click;

    public ClickGui() {
        super("ClickGui", "Opens the GUI of the client", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
    }

    public int getCurrentColorHex() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.HSBtoRGB(this.hue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    public Color getCurrentColor() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.getHSBColor(this.hue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                OctoHack.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OctoHack.commandManager.getPrefix());
            }
            OctoHack.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(OctoHackGui.getClickGui());
    }

    @Override
    public void onLoad() {
        OctoHack.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        OctoHack.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof OctoHackGui)) {
            this.disable();
        }
    }

    public static enum rainbowMode {
        Static,
        Sideway;

    }

    public static enum rainbowModeArray {
        Static,
        Up;

    }
}
