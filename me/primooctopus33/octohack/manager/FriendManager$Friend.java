package me.primooctopus33.octohack.manager;

import java.util.UUID;

public class FriendManager$Friend {
    private final String username;
    private final UUID uuid;

    public FriendManager$Friend(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    static String access$000(FriendManager$Friend x0) {
        return x0.username;
    }
}
