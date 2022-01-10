package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.modules.misc.AutoBrewer;

class AutoBrewer$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode = new int[AutoBrewer.Mode.values().length];
        try {
            AutoBrewer$1.$SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode[AutoBrewer.Mode.STRENGTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoBrewer$1.$SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode[AutoBrewer.Mode.SPEED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoBrewer$1.$SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode[AutoBrewer.Mode.SLOWNESS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoBrewer$1.$SwitchMap$me$primooctopus33$octohack$client$modules$misc$AutoBrewer$Mode[AutoBrewer.Mode.WEAKNESS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
