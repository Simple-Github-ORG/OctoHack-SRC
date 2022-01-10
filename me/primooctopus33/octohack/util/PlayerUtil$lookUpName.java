package me.primooctopus33.octohack.util;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

public class PlayerUtil$lookUpName
implements Runnable {
    private final String uuid;
    private final UUID uuidID;
    private volatile String name;

    public PlayerUtil$lookUpName(String input) {
        this.uuid = input;
        this.uuidID = UUID.fromString(input);
    }

    public PlayerUtil$lookUpName(UUID input) {
        this.uuidID = input;
        this.uuid = input.toString();
    }

    @Override
    public void run() {
        this.name = this.lookUpName();
    }

    public String lookUpName() {
        EntityPlayer player = null;
        if (Util.mc.world != null) {
            player = Util.mc.world.getPlayerEntityByUUID(this.uuidID);
        }
        if (player == null) {
            String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";
            try {
                String nameJson = IOUtils.toString(new URL(url));
                if (nameJson.contains(",")) {
                    List<String> names = Arrays.asList(nameJson.split(","));
                    Collections.reverse(names);
                    return names.get(1).replace("{\"name\":\"", "").replace("\"", "");
                }
                return nameJson.replace("[{\"name\":\"", "").replace("\"}]", "");
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return player.getName();
    }

    public String getName() {
        return this.name;
    }
}
