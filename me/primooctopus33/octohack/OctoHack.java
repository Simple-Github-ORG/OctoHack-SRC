package me.primooctopus33.octohack;

import java.util.Random;
import me.primooctopus33.octohack.DiscordPresence;
import me.primooctopus33.octohack.manager.ColorManager;
import me.primooctopus33.octohack.manager.CommandManager;
import me.primooctopus33.octohack.manager.ConfigManager;
import me.primooctopus33.octohack.manager.EventManager;
import me.primooctopus33.octohack.manager.FileManager;
import me.primooctopus33.octohack.manager.FriendManager;
import me.primooctopus33.octohack.manager.HoleManager;
import me.primooctopus33.octohack.manager.InventoryManager;
import me.primooctopus33.octohack.manager.ModuleManager;
import me.primooctopus33.octohack.manager.PacketManager;
import me.primooctopus33.octohack.manager.PositionManager;
import me.primooctopus33.octohack.manager.PotionManager;
import me.primooctopus33.octohack.manager.ReloadManager;
import me.primooctopus33.octohack.manager.RotationManager;
import me.primooctopus33.octohack.manager.SafetyManager;
import me.primooctopus33.octohack.manager.ServerManager;
import me.primooctopus33.octohack.manager.SpeedManager;
import me.primooctopus33.octohack.manager.TextManager;
import me.primooctopus33.octohack.manager.TimerManager;
import me.zero.alpine.EventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid="octohack", name="OctoHack", version="0.1.7")
public class OctoHack {
    public static Random random = new Random();
    public static final String MODID = "octohack";
    public static final String MODNAME = "OctoHack";
    public static final String MODVER = "0.1.7";
    public static final Logger LOGGER = LogManager.getLogger("OctoHack");
    public static EventBus dispatcher;
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static SafetyManager safetyManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    @Mod.Instance
    public static OctoHack INSTANCE;
    private static boolean unloaded;

    public static void load() {
        LOGGER.info("\n\nLoading OctoHack by Primooctopus33");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        dispatcher = new me.zero.alpine.EventManager();
        timerManager = new TimerManager();
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        safetyManager = new SafetyManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        LOGGER.info("Managers loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        if (moduleManager.isModuleEnabled("RPC")) {
            DiscordPresence.start();
        }
        LOGGER.info("OctoHack successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading OctoHack by Primooctopus33");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        OctoHack.onUnload();
        dispatcher = null;
        timerManager = null;
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        safetyManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        LOGGER.info("OctoHack unloaded!\n");
    }

    public static void reload() {
        OctoHack.unload(false);
        OctoHack.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(OctoHack.configManager.config.replaceFirst("octohack/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("OctoHack 0.1.7");
        OctoHack.load();
    }

    static {
        unloaded = false;
    }
}
