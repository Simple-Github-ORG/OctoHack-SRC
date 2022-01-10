package me.primooctopus33.octohack.client.command.commands;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import me.primooctopus33.octohack.client.command.Command;

public class NameMcCommand
extends Command {
    public NameMcCommand() {
        super("namemc", new String[]{"<NameMc>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        String name = commands[0];
        try {
            Desktop.getDesktop().browse(URI.create("https://namemc.com/search?q=" + name));
        }
        catch (IOException var4) {
            var4.printStackTrace();
        }
    }
}
