package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.GroundSpeed;

class GroundSpeed$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$movement$GroundSpeed$Mode;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$movement$GroundSpeed$Mode = new int[GroundSpeed.Mode.values().length];
        try {
            GroundSpeed$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$GroundSpeed$Mode[GroundSpeed.Mode.BOOST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GroundSpeed$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$GroundSpeed$Mode[GroundSpeed.Mode.ACCEL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GroundSpeed$1.$SwitchMap$me$primooctopus33$octohack$client$modules$movement$GroundSpeed$Mode[GroundSpeed.Mode.ONGROUND.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
