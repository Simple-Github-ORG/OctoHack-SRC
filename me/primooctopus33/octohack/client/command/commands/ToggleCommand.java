package me.primooctopus33.octohack.client.command.commands;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;

public class ToggleCommand
extends Command {
    public ToggleCommand() {
        super("toggle", new String[]{"<toggle>", "<module>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 2) {
            String name = commands[0].replaceAll("_", " ");
            Module module = OctoHack.moduleManager.getModuleByName(name);
            if (module != null) {
                module.toggle();
            } else {
                Command.sendMessage("Unable to find a module with that name!");
            }
        } else {
            Command.sendMessage("Please provide a valid module name!");
        }
    }
}
