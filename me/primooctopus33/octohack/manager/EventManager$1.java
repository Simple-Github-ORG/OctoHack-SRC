package me.primooctopus33.octohack.manager;

import net.minecraft.network.play.server.SPacketPlayerListItem;

class EventManager$1 {
    static final int[] $SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action;

    static {
        $SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action = new int[SPacketPlayerListItem.Action.values().length];
        try {
            EventManager$1.$SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action[SPacketPlayerListItem.Action.ADD_PLAYER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EventManager$1.$SwitchMap$net$minecraft$network$play$server$SPacketPlayerListItem$Action[SPacketPlayerListItem.Action.REMOVE_PLAYER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
