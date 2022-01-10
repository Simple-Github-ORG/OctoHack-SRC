package me.primooctopus33.octohack.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.LinkedList;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.command.commands.BindCommand;
import me.primooctopus33.octohack.client.command.commands.ConfigCommand;
import me.primooctopus33.octohack.client.command.commands.FriendCommand;
import me.primooctopus33.octohack.client.command.commands.HelpCommand;
import me.primooctopus33.octohack.client.command.commands.ModuleCommand;
import me.primooctopus33.octohack.client.command.commands.NameMcCommand;
import me.primooctopus33.octohack.client.command.commands.PrefixCommand;
import me.primooctopus33.octohack.client.command.commands.ReloadCommand;
import me.primooctopus33.octohack.client.command.commands.ReloadSoundCommand;
import me.primooctopus33.octohack.client.command.commands.ToggleCommand;
import me.primooctopus33.octohack.client.command.commands.UnloadCommand;
import me.primooctopus33.octohack.client.modules.client.HUD;

public class CommandManager
extends Feature {
    private final ArrayList<Command> commands = new ArrayList();
    private String clientMessage = "(OctoHack)";
    private String prefix = ".";

    public CommandManager() {
        super("Command");
        this.commands.add(new ToggleCommand());
        this.commands.add(new NameMcCommand());
        this.commands.add(new BindCommand());
        this.commands.add(new ModuleCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new ConfigCommand());
        this.commands.add(new FriendCommand());
        this.commands.add(new HelpCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new UnloadCommand());
        this.commands.add(new ReloadSoundCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<String>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    private static String strip(String str, String key) {
        if (str.startsWith(key) && str.endsWith(key)) {
            return str.substring(key.length(), str.length() - key.length());
        }
        return str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = CommandManager.removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) continue;
            args[i] = CommandManager.strip(args[i], "\"");
        }
        for (Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) continue;
            c.execute(parts);
            return;
        }
        Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
    }

    public Command getCommandByName(String name) {
        for (Command command : this.commands) {
            if (!command.getName().equals(name)) continue;
            return command;
        }
        return null;
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    public String getClientMessage() {
        if (HUD.getInstance().commandPrefix.getValue().booleanValue()) {
            return this.clientMessage + " ";
        }
        return " ";
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
