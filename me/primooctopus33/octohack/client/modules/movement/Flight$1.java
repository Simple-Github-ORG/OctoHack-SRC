package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.Flight;

class Flight$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode = new int[Flight.PacketMode.values().length];
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.Up.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.Down.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.Zero.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.Y.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.X.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.Z.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Flight$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Flight$PacketMode[Flight.PacketMode.XZ.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
