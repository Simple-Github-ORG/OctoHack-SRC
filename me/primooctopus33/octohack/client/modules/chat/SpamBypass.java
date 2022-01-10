package me.primooctopus33.octohack.client.modules.chat;

import me.primooctopus33.octohack.client.modules.Module;

public class SpamBypass
extends Module {
    public SpamBypass() {
        super("SpamBypass", "Attempts to bypass antispams by adding short strings of random characters after your message", Module.Category.CHAT, true, false, false);
    }
}
