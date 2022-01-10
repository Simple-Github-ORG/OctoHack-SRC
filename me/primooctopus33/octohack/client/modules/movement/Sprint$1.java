package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.Sprint;

class Sprint$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$movement$Sprint$Mode;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$movement$Sprint$Mode = new int[Sprint.Mode.values().length];
        try {
            Sprint$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Sprint$Mode[Sprint.Mode.RAGE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Sprint$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Sprint$Mode[Sprint.Mode.LEGIT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Sprint$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$Sprint$Mode[Sprint.Mode.VANILLA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
