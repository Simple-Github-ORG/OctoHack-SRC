package me.primooctopus33.octohack.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.manager.FriendManager;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/reset>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (OctoHack.friendManager.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Your friends list is currently empty!");
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : OctoHack.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    }
                    catch (Exception exception) {}
                }
                FriendCommand.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    OctoHack.friendManager.onLoad();
                    FriendCommand.sendMessage("Your friends list has been reset!");
                    return;
                }
            }
            FriendCommand.sendMessage(commands[0] + (OctoHack.friendManager.isFriend(commands[0]) ? " is your friend." : " is not your friend."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    OctoHack.friendManager.addFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.GREEN + commands[1] + " has been added to your friends list!");
                    return;
                }
                case "del": {
                    OctoHack.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.RED + commands[1] + " has been removed from your friends list!");
                    return;
                }
            }
            FriendCommand.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}
