package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.combat.BedAura;

class BedAura$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$SwitchModes;
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$Logic;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$Logic = new int[BedAura.Logic.values().length];
        try {
            BedAura$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$Logic[BedAura.Logic.BREAKPLACE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedAura$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$Logic[BedAura.Logic.PLACEBREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$SwitchModes = new int[BedAura.SwitchModes.values().length];
        try {
            BedAura$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$SwitchModes[BedAura.SwitchModes.NORMAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BedAura$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$BedAura$SwitchModes[BedAura.SwitchModes.SILENT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
