package me.primooctopus33.octohack.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.lang.invoke.LambdaMetafactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.ClientEvent;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.TextUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

public class HUD
extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static HUD INSTANCE = new HUD();
    public Setting<String> gameTitle = this.register(new Setting<String>("App Title", "OctoHack 0.1.7"));
    public Setting<Boolean> timestamp = this.register(new Setting<Boolean>("Time Stamps", true));
    private final Setting<Boolean> grayNess = this.register(new Setting<Boolean>("Gray", true));
    public final Setting<Boolean> flowingArrayList = this.register(new Setting<Boolean>("Flowing ArrayList", true));
    private final Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("Rendering Up", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    private final Setting<Boolean> waterMark = this.register(new Setting<Boolean>("Watermark", Boolean.valueOf(false), "displays watermark"));
    private final Setting<String> waterMarkName = this.register(new Setting<String>("Water Mark Name", "OctoHack"));
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("Active Modules", Boolean.valueOf(false), "Lists the active modules."));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", Boolean.valueOf(false), "TotemHUD"));
    private final Setting<Boolean> greeter = this.register(new Setting<Boolean>("Welcomer", Boolean.valueOf(false), "The time"));
    public Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", Boolean.valueOf(false), "Your Speed"));
    public Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", Boolean.valueOf(false), "Active potion effects"));
    public Setting<Boolean> potionSync = this.register(new Setting<Boolean>("Potion Sync", Boolean.valueOf(false), v -> this.potions.getValue()));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting<Boolean> lag = this.register(new Setting<Boolean>("Lag Notifier", Boolean.valueOf(false), "The time"));
    public Setting<Boolean> rainbowPrefix = this.register(new Setting<Boolean>("Rainbow Prefix", false));
    public Setting<Integer> rainbowSpeed = this.register(new Setting<Object>("Prefix Speed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> this.rainbowPrefix.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbowPrefix.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbowPrefix.getValue()));
    private final Timer timer = new Timer();
    private final Map<String, Integer> players = new HashMap<String, Integer>();
    public Setting<Boolean> commandPrefix = this.register(new Setting<Boolean>("Command Prefix", true));
    public Setting<String> command = this.register(new Setting<String>("Command", "OctoHack"));
    public Setting<TextUtil.Color> bracketColor = this.register(new Setting<TextUtil.Color>("Bracket Color", TextUtil.Color.BLUE));
    public Setting<TextUtil.Color> commandColor = this.register(new Setting<TextUtil.Color>("Name Color", TextUtil.Color.BLUE));
    public Setting<String> commandBracket = this.register(new Setting<String>("Bracket", "("));
    public Setting<String> commandBracket2 = this.register(new Setting<String>("Bracket 2", ")"));
    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("ChatNotify", Boolean.valueOf(false), "notifies in chat"));
    public Setting<Integer> animationHorizontalTime = this.register(new Setting<Object>("Animation H Time", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.arrayList.getValue()));
    public Setting<Integer> animationVerticalTime = this.register(new Setting<Object>("Animation V Time", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> this.arrayList.getValue()));
    public Setting<RenderingMode> renderingMode = this.register(new Setting<RenderingMode>("Ordering", RenderingMode.ABC));
    public Setting<Integer> waterMarkY = this.register(new Setting<Object>("Watermark Pos Y", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> this.waterMark.getValue()));
    public Setting<Boolean> time = this.register(new Setting<Boolean>("Time", Boolean.valueOf(false), "The time"));
    public Setting<Integer> lagTime = this.register(new Setting<Integer>("Lag Time", 1000, 0, 2000));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", Boolean.valueOf(false), "Universal colors for hud."));
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    public float hue;

    public HUD() {
        super("HUD", "Renders useful information on your hud", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Display.setTitle(this.gameTitle.getValue());
        int colorSpeed = 101 - this.rainbowSpeed.getValue();
        float tempHue = this.hue = (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0f * (float)colorSpeed);
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(tempHue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f));
            tempHue += 0.0013071896f;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String str1;
        String fpsText;
        ArrayList effects;
        int i;
        String grayString;
        String str;
        int j;
        if (HUD.fullNullCheck()) {
            return;
        }
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        Color color1 = new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        if (this.waterMark.getValue().booleanValue()) {
            String string = this.waterMarkName.getPlannedValue();
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue().intValue(), this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(this.waterMarkY.getValue() + 1) * 0.89f, color1) : this.color, true);
            }
        }
        int[] counter1 = new int[]{1};
        int n = j = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() == false ? 14 : 0;
        if (this.arrayList.getValue().booleanValue()) {
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < OctoHack.moduleManager.sortedModulesABC.size(); ++k) {
                        String str2 = OctoHack.moduleManager.sortedModulesABC.get(k);
                        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                            this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), 2 + j * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        } else {
                            this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), 2 + j * 10, this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(j + 1) * 0.89f, color1) : this.color, true);
                        }
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < OctoHack.moduleManager.sortedModules.size(); ++k) {
                        Module module = OctoHack.moduleManager.sortedModules.get(k);
                        str = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                            this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), 2 + j * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        } else {
                            this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), 2 + j * 10, this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(j + 1) * 0.89f, color1) : this.color, true);
                        }
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < OctoHack.moduleManager.sortedModulesABC.size(); ++k) {
                    String str3 = OctoHack.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                        this.renderer.drawString(str3, width - 2 - this.renderer.getStringWidth(str3), height - j, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    } else {
                        this.renderer.drawString(str3, width - 2 - this.renderer.getStringWidth(str3), height - j, this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(j + 1) * 0.89f, color1) : this.color, true);
                    }
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < OctoHack.moduleManager.sortedModules.size(); ++k) {
                    Module module = OctoHack.moduleManager.sortedModules.get(k);
                    str = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    j += 10;
                    if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                        this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), height - j, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    } else {
                        this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), height - j, this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(j + 1) * 0.89f, color1) : this.color, true);
                    }
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        String string = grayString = this.grayNess.getValue() != false ? String.valueOf(ChatFormatting.GRAY) : "";
        int n2 = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str4 = OctoHack.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str4, width - this.renderer.getStringWidth(str4) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() != false ? this.color : potionEffect.getPotion().getLiquidColor()), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                str = grayString + "Speed " + ChatFormatting.WHITE + OctoHack.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                str = grayString + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                str = grayString + "TPS " + ChatFormatting.WHITE + OctoHack.serverManager.getTPS();
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OctoHack.serverManager.getPing() + "ms";
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str5 = OctoHack.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str5, width - this.renderer.getStringWidth(str5) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() != false ? this.color : potionEffect.getPotion().getLiquidColor()), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                str = grayString + "Speed " + ChatFormatting.WHITE + OctoHack.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                str = grayString + " Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                str = grayString + "TPS " + ChatFormatting.WHITE + OctoHack.serverManager.getTPS();
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OctoHack.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int)HUD.mc.player.posX;
        int posY = (int)HUD.mc.player.posY;
        int posZ = (int)HUD.mc.player.posZ;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(HUD.mc.player.posX * (double)nether);
        int hposZ = (int)(HUD.mc.player.posZ * (double)nether);
        i = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = ChatFormatting.RESET + (inHell ? String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + "," + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]" : String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]");
        String direction = this.direction.getValue() != false ? OctoHack.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue() != false ? coordinates : "";
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            String rainbowCoords;
            String string2 = this.coords.getValue().booleanValue() ? (inHell ? posX + " [" + hposX + "], " + posY + " , " + posZ + " [" + hposZ + "]" : posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]") : (rainbowCoords = "");
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, height - i - 11, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0f, height - i, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter2 = new int[]{1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + s, height - i - 11, ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = new int[]{1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + u, height - i, ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0f, height - i - 11, this.color, true);
            this.renderer.drawString(coords, 2.0f, height - i, this.color, true);
        }
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(true);
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue().booleanValue()) {
            this.renderGreeter();
        }
        if (this.lag.getValue().booleanValue()) {
            this.renderLag();
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        Color color1 = new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        String text = "";
        if (this.greeter.getValue().booleanValue()) {
            text = "Welcome to OctoHack :^) " + HUD.mc.player.getDisplayNameString();
        }
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter1 = new int[]{1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f + i, 2.0f, ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    i += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.flowingArrayList.getValue() != false ? ColorUtil.staticRainbow(2.67f, color1) : this.color, true);
        }
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (OctoHack.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "Server not responding " + MathUtil.round((float)OctoHack.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt((ToIntFunction<ItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getCount(), (Lnet/minecraft/item/ItemStack;)I)()).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            boolean iteration = false;
            int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            int dmg = 0;
            int itemDurability = is.getMaxDamage() - is.getItemDamage();
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            dmg = percent ? 100 - (int)(red * 100.0f) : itemDurability;
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        this.shouldIncrement = true;
    }

    @Override
    public void onLoad() {
        OctoHack.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
            OctoHack.commandManager.setClientMessage(this.getCommandMessage());
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat)event.getPacket()).isSystem()) {
                return;
            }
            if (this.timestamp.getValue().booleanValue()) {
                String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
                String message = this.getTimeString(originalMessage) + originalMessage;
                ((SPacketChat)event.getPacket()).chatComponent = new TextComponentString(message);
            }
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("h:mm").format(new Date());
        String timeString = "<" + date + "> ";
        StringBuilder builder = new StringBuilder(timeString);
        builder.insert(0, "\u00a7+");
        builder.append("\u00a7r");
        return builder.toString();
    }

    public String getCommandMessage() {
        if (this.rainbowPrefix.getPlannedValue().booleanValue()) {
            StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
            stringBuilder.insert(0, "\u00a7+");
            stringBuilder.append("\u00a7r");
            return stringBuilder.toString();
        }
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return this.commandBracket.getValue() + this.command.getValue() + this.commandBracket2.getValue();
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, y, this.color, true);
                y += textheight;
            }
        }
    }

    public static enum RenderingMode {
        Length,
        ABC;

    }
}
