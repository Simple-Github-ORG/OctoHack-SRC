package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.combat.Burrow;

class Burrow$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$Burrow$Mode;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$Burrow$Mode = new int[Burrow.Mode.values().length];
        try {
            Burrow$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$Burrow$Mode[Burrow.Mode.OBBY.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Burrow$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$Burrow$Mode[Burrow.Mode.ECHEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Burrow$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$Burrow$Mode[Burrow.Mode.EABypass.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
