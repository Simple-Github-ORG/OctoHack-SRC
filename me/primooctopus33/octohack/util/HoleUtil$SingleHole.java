package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.HoleUtil;
import net.minecraft.util.math.BlockPos;

public final class HoleUtil$SingleHole
extends HoleUtil.Hole {
    public BlockPos pos;

    public HoleUtil$SingleHole(BlockPos pos, HoleUtil.material mat) {
        super(HoleUtil.type.SINGLE, mat);
        this.pos = pos;
    }
}
