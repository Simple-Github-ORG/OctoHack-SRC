package me.primooctopus33.octohack.client.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.client.HUD;
import me.primooctopus33.octohack.client.setting.Bind;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.event.Event;
import me.primooctopus33.octohack.event.events.ClientEvent;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module
extends Feature {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = this.register(new Setting<Boolean>("Enabled", false));
    public Setting<Boolean> drawn = this.register(new Setting<Boolean>("Drawn", true));
    public Setting<Bind> bind = this.register(new Setting<Bind>("Keybind", new Bind(-1)));
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    public float arrayListOffset = 0.0f;
    public float arrayListVOffset = 0.0f;
    public float offset;
    public float vOffset;
    public boolean sliding;
    public static Module INSTANCE;

    public void init() {
    }

    public void onEvent(Event<?> e) {
    }

    public Module(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name);
        this.displayName = this.register(new Setting<String>("DisplayName", name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onUnload() {
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return this.enabled.getValue() == false;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public String getTimeString() {
        String date = new SimpleDateFormat("h:mm").format(new Date());
        String timeString = "<" + date + ">";
        StringBuilder builder = new StringBuilder(timeString);
        builder.insert(0, "\u00a7+");
        builder.append("\u00a7r");
        return builder.toString();
    }

    public void enable() {
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();
        if (HUD.getInstance().notifyToggles.getValue().booleanValue()) {
            if (HUD.getInstance().timestamp.getValue().booleanValue()) {
                TextComponentString text = new TextComponentString(this.getTimeString() + OctoHack.commandManager.getClientMessage() + " " + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + ChatFormatting.BOLD + " Toggled ON!");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            } else if (!HUD.getInstance().timestamp.getValue().booleanValue()) {
                TextComponentString text = new TextComponentString(OctoHack.commandManager.getClientMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + ChatFormatting.BOLD + " Toggled ON!");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            }
        }
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.enabled.setValue(false);
        if (HUD.getInstance().notifyToggles.getValue().booleanValue()) {
            if (HUD.getInstance().timestamp.getValue().booleanValue()) {
                TextComponentString text = new TextComponentString(this.getTimeString() + OctoHack.commandManager.getClientMessage() + " " + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + ChatFormatting.BOLD + " Toggled OFF!");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            } else if (!HUD.getInstance().timestamp.getValue().booleanValue()) {
                TextComponentString text = new TextComponentString(OctoHack.commandManager.getClientMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + ChatFormatting.BOLD + " Toggled OFF!");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            }
        }
        this.onToggle();
        this.onDisable();
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }

    public void setDisplayName(String name) {
        Module module = OctoHack.moduleManager.getModuleByDisplayName(name);
        Module originalModule = OctoHack.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return this.hasListener && this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + (this.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + this.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
    }

    public static enum Category {
        CHAT("Chat"),
        COMBAT("Combat"),
        EXPLOIT("Exploit"),
        MISC("Misc"),
        RENDER("Render"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CLIENT("Client");

        private final String name;

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
