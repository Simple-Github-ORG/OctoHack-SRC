package me.primooctopus33.octohack.client.modules;

public enum Module$Category {
    CHAT("Chat"),
    COMBAT("Combat"),
    EXPLOIT("Exploit"),
    MISC("Misc"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    CLIENT("Client");

    private final String name;

    private Module$Category(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
