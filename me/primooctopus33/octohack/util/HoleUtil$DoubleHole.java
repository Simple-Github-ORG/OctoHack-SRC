package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.HoleUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public final class HoleUtil$DoubleHole
extends HoleUtil.Hole {
    public BlockPos pos;
    public BlockPos pos1;
    public EnumFacing dir;

    public HoleUtil$DoubleHole(BlockPos pos, BlockPos pos1, HoleUtil.material mat, EnumFacing dir) {
        super(HoleUtil.type.DOUBLE, mat);
        this.pos = pos;
        this.pos1 = pos1;
        this.dir = dir;
    }

    public boolean contains(BlockPos pos) {
        if (this.pos == pos) {
            return true;
        }
        return this.pos1 == pos;
    }

    public boolean contains(HoleUtil$DoubleHole pos) {
        if (pos.pos.equals(this.pos) || pos.pos.equals(this.pos1)) {
            return true;
        }
        return pos.pos1.equals(this.pos) || pos.pos1.equals(this.pos1);
    }

    public boolean equals(HoleUtil$DoubleHole pos) {
        return !(!pos.pos1.equals(this.pos) && !pos.pos1.equals(this.pos1) || !pos.pos.equals(this.pos) && !pos.pos.equals(this.pos1));
    }
}
