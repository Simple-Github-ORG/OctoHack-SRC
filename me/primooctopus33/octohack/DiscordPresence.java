package me.primooctopus33.octohack;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.primooctopus33.octohack.client.modules.misc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static Thread thread;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("916082988335390772", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + "." : " multiplayer.") : " singleplayer.");
        DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
        DiscordPresence.presence.largeImageText = RPC.INSTANCE.largeImageText.getValue();
        DiscordPresence.presence.smallImageKey = RPC.INSTANCE.smallImage.getValue().toString();
        DiscordPresence.presence.largeImageKey = RPC.INSTANCE.largeImage.getValue().toString();
        DiscordPresence.presence.smallImageText = RPC.INSTANCE.smallImageText.getValue();
        DiscordPresence.presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        DiscordPresence.presence.partyMax = 50;
        DiscordPresence.presence.partySize = 1;
        DiscordPresence.presence.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM=";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                String string = "";
                StringBuilder sb = new StringBuilder();
                DiscordRichPresence presence = presence;
                new StringBuilder().append("Playing ");
                string = Minecraft.getMinecraft().getCurrentServerData() != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().getCurrentServerData().serverIP + "." : " multiplayer.") : " not multiplayer.";
                presence.details = sb.append(string).toString();
                DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}
