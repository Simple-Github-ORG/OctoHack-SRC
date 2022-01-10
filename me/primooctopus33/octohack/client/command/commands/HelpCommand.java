package me.primooctopus33.octohack.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : OctoHack.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + OctoHack.commandManager.getPrefix() + command.getName());
        }
    }
}
