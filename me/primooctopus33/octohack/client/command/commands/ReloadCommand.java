package me.primooctopus33.octohack.client.command.commands;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;

public class ReloadCommand
extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        OctoHack.reload();
    }
}
