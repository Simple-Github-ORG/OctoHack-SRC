package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.combat.AutoCrystal;
import net.minecraft.util.math.RayTraceResult;

class AutoCrystal$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Logic;
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$AutoSwitch;
    static final int[] $SwitchMap$net$minecraft$util$math$RayTraceResult$Type;
    static final int[] $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate;

    static {
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate = new int[AutoCrystal.Rotate.values().length];
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate[AutoCrystal.Rotate.OFF.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate[AutoCrystal.Rotate.BREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate[AutoCrystal.Rotate.ALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Rotate[AutoCrystal.Rotate.PLACE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$util$math$RayTraceResult$Type = new int[RayTraceResult.Type.values().length];
        try {
            AutoCrystal$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.ENTITY.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.BLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$AutoSwitch = new int[AutoCrystal.AutoSwitch.values().length];
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$AutoSwitch[AutoCrystal.AutoSwitch.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$AutoSwitch[AutoCrystal.AutoSwitch.TOGGLE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$AutoSwitch[AutoCrystal.AutoSwitch.ALWAYS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Logic = new int[AutoCrystal.Logic.values().length];
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Logic[AutoCrystal.Logic.BREAKPLACE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoCrystal$1.$SwitchMap$me$primooctopus33$octohack$client$modules$combat$AutoCrystal$Logic[AutoCrystal.Logic.PLACEBREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
