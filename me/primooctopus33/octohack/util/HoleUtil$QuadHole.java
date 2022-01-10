package me.primooctopus33.octohack.util;

import me.primooctopus33.octohack.util.HoleUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public final class HoleUtil$QuadHole
extends HoleUtil.Hole {
    public BlockPos pos;
    public BlockPos pos1;
    public BlockPos pos2;
    public BlockPos pos3;
    public EnumFacing dir;

    public HoleUtil$QuadHole(BlockPos pos, BlockPos pos1, BlockPos pos2, BlockPos pos3, HoleUtil.material mat, EnumFacing dir) {
        super(HoleUtil.type.QUAD, mat);
        this.pos = pos;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.dir = dir;
    }

    public boolean contains(BlockPos pos) {
        if (this.pos == pos) {
            return true;
        }
        if (this.pos1 == this.pos1) {
            return true;
        }
        if (this.pos2 == this.pos2) {
            return true;
        }
        return this.pos3 == pos;
    }

    public boolean contains(HoleUtil$QuadHole pos) {
        if (pos.pos.equals(this.pos) || pos.pos.equals(this.pos1) || pos.pos.equals(this.pos2)) {
            return true;
        }
        return pos.pos3.equals(this.pos) || pos.pos1.equals(this.pos3);
    }

    public boolean equals(HoleUtil$QuadHole pos) {
        return pos.pos3.equals(this.pos) || pos.pos3.equals(this.pos3) || pos.pos2.equals(this.pos) || pos.pos2.equals(this.pos2) || pos.pos1.equals(this.pos) || pos.pos1.equals(this.pos1) && (pos.pos.equals(this.pos) || pos.pos.equals(this.pos3) || pos.pos.equals(this.pos2) || pos.pos.equals(this.pos1));
    }
}
