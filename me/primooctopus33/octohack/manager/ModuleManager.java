package me.primooctopus33.octohack.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.gui.OctoHackGui;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.chat.AntiBully;
import me.primooctopus33.octohack.client.modules.chat.AntiLog4j;
import me.primooctopus33.octohack.client.modules.chat.AutoAmogus;
import me.primooctopus33.octohack.client.modules.chat.AutoBully;
import me.primooctopus33.octohack.client.modules.chat.AutoCope;
import me.primooctopus33.octohack.client.modules.chat.AutoFitFag;
import me.primooctopus33.octohack.client.modules.chat.ChatModifier;
import me.primooctopus33.octohack.client.modules.chat.MyCode;
import me.primooctopus33.octohack.client.modules.chat.PotionAlert;
import me.primooctopus33.octohack.client.modules.chat.VisualRange;
import me.primooctopus33.octohack.client.modules.chat.WordGuard;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.modules.client.CrystalPvPBot;
import me.primooctopus33.octohack.client.modules.client.FontMod;
import me.primooctopus33.octohack.client.modules.client.HUD;
import me.primooctopus33.octohack.client.modules.client.IllegalStackDupeButton;
import me.primooctopus33.octohack.client.modules.client.NickHider;
import me.primooctopus33.octohack.client.modules.client.PacketsCounter;
import me.primooctopus33.octohack.client.modules.client.TargetHud;
import me.primooctopus33.octohack.client.modules.combat.AntiBowBomb;
import me.primooctopus33.octohack.client.modules.combat.AntiFacePlace;
import me.primooctopus33.octohack.client.modules.combat.AntiRegear;
import me.primooctopus33.octohack.client.modules.combat.AntiSurround;
import me.primooctopus33.octohack.client.modules.combat.AnvilCev;
import me.primooctopus33.octohack.client.modules.combat.AutoArmor;
import me.primooctopus33.octohack.client.modules.combat.AutoClicker;
import me.primooctopus33.octohack.client.modules.combat.AutoCrystal;
import me.primooctopus33.octohack.client.modules.combat.AutoHoleFill;
import me.primooctopus33.octohack.client.modules.combat.AutoPlaceObsidian;
import me.primooctopus33.octohack.client.modules.combat.AutoPot;
import me.primooctopus33.octohack.client.modules.combat.AutoTrap;
import me.primooctopus33.octohack.client.modules.combat.AutoWeb;
import me.primooctopus33.octohack.client.modules.combat.BedAura;
import me.primooctopus33.octohack.client.modules.combat.BedAuraThirteen;
import me.primooctopus33.octohack.client.modules.combat.BowAim;
import me.primooctopus33.octohack.client.modules.combat.Burrow;
import me.primooctopus33.octohack.client.modules.combat.CCBurrowBypass;
import me.primooctopus33.octohack.client.modules.combat.CevBreaker;
import me.primooctopus33.octohack.client.modules.combat.Criticals;
import me.primooctopus33.octohack.client.modules.combat.CrystalPredict;
import me.primooctopus33.octohack.client.modules.combat.Flatten;
import me.primooctopus33.octohack.client.modules.combat.HephaestusCA;
import me.primooctopus33.octohack.client.modules.combat.HoleFiller;
import me.primooctopus33.octohack.client.modules.combat.Killaura;
import me.primooctopus33.octohack.client.modules.combat.Offhand;
import me.primooctopus33.octohack.client.modules.combat.PistonCrystal;
import me.primooctopus33.octohack.client.modules.combat.ProjectileBomb;
import me.primooctopus33.octohack.client.modules.combat.Selftrap;
import me.primooctopus33.octohack.client.modules.combat.ShulkerCrystal;
import me.primooctopus33.octohack.client.modules.combat.SilentAutoXP;
import me.primooctopus33.octohack.client.modules.combat.SmartCity;
import me.primooctopus33.octohack.client.modules.combat.Surround;
import me.primooctopus33.octohack.client.modules.combat.SurroundRewrite;
import me.primooctopus33.octohack.client.modules.exploit.AntiHunger;
import me.primooctopus33.octohack.client.modules.exploit.AutoRubberband;
import me.primooctopus33.octohack.client.modules.exploit.ChorusLag;
import me.primooctopus33.octohack.client.modules.exploit.ChorusPostpone;
import me.primooctopus33.octohack.client.modules.exploit.Clip;
import me.primooctopus33.octohack.client.modules.exploit.HeightLimit;
import me.primooctopus33.octohack.client.modules.exploit.ManualIllegalStack;
import me.primooctopus33.octohack.client.modules.exploit.PacketCanceller;
import me.primooctopus33.octohack.client.modules.exploit.PacketsLogger;
import me.primooctopus33.octohack.client.modules.exploit.PingSpoof;
import me.primooctopus33.octohack.client.modules.exploit.PortalModifier;
import me.primooctopus33.octohack.client.modules.exploit.Speedmine;
import me.primooctopus33.octohack.client.modules.exploit.TickShift;
import me.primooctopus33.octohack.client.modules.misc.AnvilBurrowNuker;
import me.primooctopus33.octohack.client.modules.misc.AutoBedTrap;
import me.primooctopus33.octohack.client.modules.misc.AutoBowRelease;
import me.primooctopus33.octohack.client.modules.misc.AutoBrewer;
import me.primooctopus33.octohack.client.modules.misc.AutoFish;
import me.primooctopus33.octohack.client.modules.misc.AutoKit;
import me.primooctopus33.octohack.client.modules.misc.AutoSelfAnvil;
import me.primooctopus33.octohack.client.modules.misc.FakePlayer;
import me.primooctopus33.octohack.client.modules.misc.GhastFinder;
import me.primooctopus33.octohack.client.modules.misc.GhastTracer;
import me.primooctopus33.octohack.client.modules.misc.MCF;
import me.primooctopus33.octohack.client.modules.misc.NoHandShake;
import me.primooctopus33.octohack.client.modules.misc.PearlNotify;
import me.primooctopus33.octohack.client.modules.misc.PluginsGrabber;
import me.primooctopus33.octohack.client.modules.misc.PopCounter;
import me.primooctopus33.octohack.client.modules.misc.Quiver;
import me.primooctopus33.octohack.client.modules.misc.RPC;
import me.primooctopus33.octohack.client.modules.misc.Tracker;
import me.primooctopus33.octohack.client.modules.misc.XCarry;
import me.primooctopus33.octohack.client.modules.movement.AntiSlowDown;
import me.primooctopus33.octohack.client.modules.movement.AntiVoid;
import me.primooctopus33.octohack.client.modules.movement.AutoStairJump;
import me.primooctopus33.octohack.client.modules.movement.ElytraFly;
import me.primooctopus33.octohack.client.modules.movement.Flight;
import me.primooctopus33.octohack.client.modules.movement.GroundSpeed;
import me.primooctopus33.octohack.client.modules.movement.InventoryWalk;
import me.primooctopus33.octohack.client.modules.movement.NoFall;
import me.primooctopus33.octohack.client.modules.movement.NoPush;
import me.primooctopus33.octohack.client.modules.movement.PhaseWalk;
import me.primooctopus33.octohack.client.modules.movement.ReverseStep;
import me.primooctopus33.octohack.client.modules.movement.Scaffold;
import me.primooctopus33.octohack.client.modules.movement.Sprint;
import me.primooctopus33.octohack.client.modules.movement.Step;
import me.primooctopus33.octohack.client.modules.movement.Velocity;
import me.primooctopus33.octohack.client.modules.player.AntiAim;
import me.primooctopus33.octohack.client.modules.player.AutoLog;
import me.primooctopus33.octohack.client.modules.player.FastPlace;
import me.primooctopus33.octohack.client.modules.player.MCP;
import me.primooctopus33.octohack.client.modules.player.NoEntityTrace;
import me.primooctopus33.octohack.client.modules.player.NoGlitchBlocks;
import me.primooctopus33.octohack.client.modules.player.NoRotate;
import me.primooctopus33.octohack.client.modules.player.Replenish;
import me.primooctopus33.octohack.client.modules.player.RespawnModifier;
import me.primooctopus33.octohack.client.modules.player.SplashPotRefill;
import me.primooctopus33.octohack.client.modules.player.Timers;
import me.primooctopus33.octohack.client.modules.render.Ambience;
import me.primooctopus33.octohack.client.modules.render.Animations;
import me.primooctopus33.octohack.client.modules.render.AspectRatio;
import me.primooctopus33.octohack.client.modules.render.BlockHighlight;
import me.primooctopus33.octohack.client.modules.render.BlockView;
import me.primooctopus33.octohack.client.modules.render.BreadCrumbs;
import me.primooctopus33.octohack.client.modules.render.Chams;
import me.primooctopus33.octohack.client.modules.render.ChorusFinder;
import me.primooctopus33.octohack.client.modules.render.CrystalChams;
import me.primooctopus33.octohack.client.modules.render.CustomCrosshair;
import me.primooctopus33.octohack.client.modules.render.ESP;
import me.primooctopus33.octohack.client.modules.render.ExplosionChams;
import me.primooctopus33.octohack.client.modules.render.FreeF5;
import me.primooctopus33.octohack.client.modules.render.Fullbright;
import me.primooctopus33.octohack.client.modules.render.HandView;
import me.primooctopus33.octohack.client.modules.render.HoleESP;
import me.primooctopus33.octohack.client.modules.render.Ink;
import me.primooctopus33.octohack.client.modules.render.Nametags;
import me.primooctopus33.octohack.client.modules.render.NoRender;
import me.primooctopus33.octohack.client.modules.render.PopChams;
import me.primooctopus33.octohack.client.modules.render.ShaderChams;
import me.primooctopus33.octohack.client.modules.render.ShulkerTips;
import me.primooctopus33.octohack.client.modules.render.Skeleton;
import me.primooctopus33.octohack.client.modules.render.SkyColor;
import me.primooctopus33.octohack.client.modules.render.StorageESP;
import me.primooctopus33.octohack.client.modules.render.Trails;
import me.primooctopus33.octohack.client.modules.render.Trajectories;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.Util;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ModuleManager
extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<String>();
    public Animation animationThread;

    public void init() {
        this.modules.add(new AntiBully());
        this.modules.add(new SurroundRewrite());
        this.modules.add(new NoEntityTrace());
        this.modules.add(new Ambience());
        this.modules.add(new Ink());
        this.modules.add(new AutoBedTrap());
        this.modules.add(new PluginsGrabber());
        this.modules.add(new ShaderChams());
        this.modules.add(new MyCode());
        this.modules.add(new AntiBowBomb());
        this.modules.add(new AutoClicker());
        this.modules.add(new Sprint());
        this.modules.add(new AntiLog4j());
        this.modules.add(new PacketsCounter());
        this.modules.add(new HephaestusCA());
        this.modules.add(new ElytraFly());
        this.modules.add(new ExplosionChams());
        this.modules.add(new TargetHud());
        this.modules.add(new NoRotate());
        this.modules.add(new ReverseStep());
        this.modules.add(new HoleESP());
        this.modules.add(new GhastTracer());
        this.modules.add(new AutoCope());
        this.modules.add(new AutoFitFag());
        this.modules.add(new SmartCity());
        this.modules.add(new WordGuard());
        this.modules.add(new AutoBowRelease());
        this.modules.add(new AutoBully());
        this.modules.add(new TickShift());
        this.modules.add(new Animations());
        this.modules.add(new Clip());
        this.modules.add(new AntiAim());
        this.modules.add(new BreadCrumbs());
        this.modules.add(new AutoStairJump());
        this.modules.add(new PistonCrystal());
        this.modules.add(new AntiSurround());
        this.modules.add(new Timers());
        this.modules.add(new Speedmine());
        this.modules.add(new AutoPlaceObsidian());
        this.modules.add(new CrystalChams());
        this.modules.add(new Quiver());
        this.modules.add(new AntiFacePlace());
        this.modules.add(new BowAim());
        this.modules.add(new AutoFish());
        this.modules.add(new Flight());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoKit());
        this.modules.add(new Skeleton());
        this.modules.add(new ESP());
        this.modules.add(new ProjectileBomb());
        this.modules.add(new FreeF5());
        this.modules.add(new StorageESP());
        this.modules.add(new XCarry());
        this.modules.add(new AutoAmogus());
        this.modules.add(new PingSpoof());
        this.modules.add(new ChorusPostpone());
        this.modules.add(new CrystalPvPBot());
        this.modules.add(new AutoBrewer());
        this.modules.add(new AutoSelfAnvil());
        this.modules.add(new AnvilCev());
        this.modules.add(new ChorusLag());
        this.modules.add(new PopChams());
        this.modules.add(new PhaseWalk());
        this.modules.add(new CevBreaker());
        this.modules.add(new Trajectories());
        this.modules.add(new HandView());
        this.modules.add(new BlockView());
        this.modules.add(new CCBurrowBypass());
        this.modules.add(new BedAuraThirteen());
        this.modules.add(new NoPush());
        this.modules.add(new GhastFinder());
        this.modules.add(new Scaffold());
        this.modules.add(new BedAura());
        this.modules.add(new ChorusFinder());
        this.modules.add(new InventoryWalk());
        this.modules.add(new ShulkerCrystal());
        this.modules.add(new CustomCrosshair());
        this.modules.add(new SilentAutoXP());
        this.modules.add(new AntiRegear());
        this.modules.add(new AnvilBurrowNuker());
        this.modules.add(new AutoPot());
        this.modules.add(new SplashPotRefill());
        this.modules.add(new FakePlayer());
        this.modules.add(new PacketsLogger());
        this.modules.add(new PotionAlert());
        this.modules.add(new Surround());
        this.modules.add(new AutoHoleFill());
        this.modules.add(new VisualRange());
        this.modules.add(new ManualIllegalStack());
        this.modules.add(new IllegalStackDupeButton());
        this.modules.add(new PortalModifier());
        this.modules.add(new AutoLog());
        this.modules.add(new PacketCanceller());
        this.modules.add(new RespawnModifier());
        this.modules.add(new NoHandShake());
        this.modules.add(new AutoRubberband());
        this.modules.add(new AntiSlowDown());
        this.modules.add(new Flatten());
        this.modules.add(new Step());
        this.modules.add(new AntiHunger());
        this.modules.add(new Replenish());
        this.modules.add(new Fullbright());
        this.modules.add(new Velocity());
        this.modules.add(new CrystalPredict());
        this.modules.add(new Chams());
        this.modules.add(new Nametags());
        this.modules.add(new ChatModifier());
        this.modules.add(new GroundSpeed());
        this.modules.add(new NoRender());
        this.modules.add(new NickHider());
        this.modules.add(new SkyColor());
        this.modules.add(new AspectRatio());
        this.modules.add(new Burrow());
        this.modules.add(new RPC());
        this.modules.add(new ClickGui());
        this.modules.add(new FontMod());
        this.modules.add(new HUD());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trails());
        this.modules.add(new MCP());
        this.modules.add(new AntiVoid());
        this.modules.add(new MCF());
        this.modules.add(new PearlNotify());
        this.modules.add(new ShulkerTips());
        this.modules.add(new Tracker());
        this.modules.add(new HeightLimit());
        this.modules.add(new PopCounter());
        this.modules.add(new Offhand());
        this.modules.add(new AutoTrap());
        this.modules.add(new AutoWeb());
        this.modules.add(new Killaura());
        this.modules.add(new Criticals());
        this.modules.add(new HoleFiller());
        this.modules.add(new AutoArmor());
        this.modules.add(new FastPlace());
        this.modules.add(new Selftrap());
        this.modules.add(new NoGlitchBlocks());
        this.modules.add(new NoFall());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<String>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof OctoHackGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    private class Animation
    extends Thread {
        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service;

        public Animation() {
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
}
