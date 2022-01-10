package me.primooctopus33.octohack.client.command.commands;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;

public class UnloadCommand
extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        OctoHack.unload(true);
    }
}
