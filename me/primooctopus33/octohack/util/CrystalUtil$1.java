package me.primooctopus33.octohack.util;

import net.minecraft.world.EnumDifficulty;

class CrystalUtil$1 {
    static final int[] $SwitchMap$net$minecraft$world$EnumDifficulty;

    static {
        $SwitchMap$net$minecraft$world$EnumDifficulty = new int[EnumDifficulty.values().length];
        try {
            CrystalUtil$1.$SwitchMap$net$minecraft$world$EnumDifficulty[EnumDifficulty.PEACEFUL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrystalUtil$1.$SwitchMap$net$minecraft$world$EnumDifficulty[EnumDifficulty.EASY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrystalUtil$1.$SwitchMap$net$minecraft$world$EnumDifficulty[EnumDifficulty.HARD.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
