package me.primooctopus33.octohack.client.modules.player;

import net.minecraft.util.math.RayTraceResult;

class FastPlace$1 {
    static final int[] $SwitchMap$net$minecraft$util$math$RayTraceResult$Type;

    static {
        $SwitchMap$net$minecraft$util$math$RayTraceResult$Type = new int[RayTraceResult.Type.values().length];
        try {
            FastPlace$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.MISS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FastPlace$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.BLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FastPlace$1.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[RayTraceResult.Type.ENTITY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
