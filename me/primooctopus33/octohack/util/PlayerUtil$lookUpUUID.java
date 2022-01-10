package me.primooctopus33.octohack.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.util.PlayerUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerUtil$lookUpUUID
implements Runnable {
    private final String name;
    private volatile UUID uuid;

    public PlayerUtil$lookUpUUID(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        NetworkPlayerInfo profile;
        try {
            ArrayList infoMap = new ArrayList(Objects.requireNonNull(Util.mc.getConnection()).getPlayerInfoMap());
            profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
            assert (profile != null);
            this.uuid = profile.getGameProfile().getId();
        }
        catch (Exception e) {
            profile = null;
        }
        if (profile == null) {
            Command.sendMessage("Player isn't online. Looking up UUID..");
            String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
            if (s == null || s.isEmpty()) {
                Command.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
            } else {
                JsonElement element = new JsonParser().parse(s);
                if (element.getAsJsonArray().size() == 0) {
                    Command.sendMessage("Couldn't find player ID. (1)");
                } else {
                    try {
                        String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                        this.uuid = UUIDTypeAdapter.fromString((String)id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Command.sendMessage("Couldn't find player ID. (2)");
                    }
                }
            }
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }
}
