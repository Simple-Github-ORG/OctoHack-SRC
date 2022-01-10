package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender
extends Module {
    private static NoRender INSTANCE = new NoRender();
    public Setting<NoArmor> noArmor = this.register(new Setting<NoArmor>("NoArmor", NoArmor.NONE, "Doesnt Render Armor on players."));
    public Setting<Skylight> skylight = this.register(new Setting<Skylight>("Skylight", Skylight.NONE));
    public Setting<Boolean> advancements = this.register(new Setting<Boolean>("Advancements", false));
    public Setting<Boolean> hypixelAdvancements = this.register(new Setting<Boolean>("Hypixel Achievements", true));
    public Setting<Boolean> hurtCam = this.register(new Setting<Boolean>("NoHurtCam", false));
    public Setting<Boolean> fire = this.register(new Setting<Boolean>("Fire", Boolean.valueOf(false), "Removes the portal overlay."));

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onPacketRecieve(PacketEvent.Receive event) {
        String text;
        if (event.getPacket() instanceof SPacketChat && this.hypixelAdvancements.getValue().booleanValue() && (text = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText()).contains("Achievement Unlocked: ") && text.contains(">>") && text.contains("<<")) {
            event.setCanceled(true);
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    static {
        INSTANCE = new NoRender();
    }

    public static enum NoArmor {
        NONE,
        ALL,
        HELMET;

    }

    public static enum Skylight {
        NONE,
        WORLD,
        ENTITY,
        ALL;

    }
}
