package me.primooctopus33.octohack.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;

public class PrefixCommand
extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + OctoHack.commandManager.getPrefix());
            return;
        }
        OctoHack.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
    }
}
