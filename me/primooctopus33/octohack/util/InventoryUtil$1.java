package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.InventoryUtil;

class InventoryUtil$1 {
    static final int[] $SwitchMap$me$primooctopus33$octohack$util$InventoryUtil$Switch;

    static {
        $SwitchMap$me$primooctopus33$octohack$util$InventoryUtil$Switch = new int[InventoryUtil.Switch.values().length];
        try {
            InventoryUtil$1.$SwitchMap$me$primooctopus33$octohack$util$InventoryUtil$Switch[InventoryUtil.Switch.NORMAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            InventoryUtil$1.$SwitchMap$me$primooctopus33$octohack$util$InventoryUtil$Switch[InventoryUtil.Switch.SILENT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            InventoryUtil$1.$SwitchMap$me$primooctopus33$octohack$util$InventoryUtil$Switch[InventoryUtil.Switch.NONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
