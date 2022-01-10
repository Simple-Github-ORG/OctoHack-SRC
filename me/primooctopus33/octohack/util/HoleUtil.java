package me.primooctopus33.octohack.util;

import java.util.ArrayList;
import me.primooctopus33.octohack.util.PlayerUtil;
import me.primooctopus33.octohack.util.Util;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class HoleUtil
implements Util {
    public static Hole getHole(BlockPos pos, int height) {
        boolean a = false;
        for (int b = 0; b < height; ++b) {
            if (WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, b + 1, 0)))) continue;
            a = true;
        }
        if (a) {
            return null;
        }
        if (WorldUtils.empty.contains(WorldUtils.getBlock(pos)) && !WorldUtils.empty.contains(WorldUtils.getBlock(pos.down()))) {
            if (!(!(WorldUtils.getBlock(pos.north()) instanceof BlockObsidian) && WorldUtils.getBlock(pos.north()) != Blocks.BEDROCK || !(WorldUtils.getBlock(pos.south()) instanceof BlockObsidian) && WorldUtils.getBlock(pos.south()) != Blocks.BEDROCK || !(WorldUtils.getBlock(pos.east()) instanceof BlockObsidian) && WorldUtils.getBlock(pos.east()) != Blocks.BEDROCK || !(WorldUtils.getBlock(pos.west()) instanceof BlockObsidian) && WorldUtils.getBlock(pos.west()) != Blocks.BEDROCK)) {
                if (WorldUtils.getBlock(pos.north()) instanceof BlockObsidian || WorldUtils.getBlock(pos.east()) instanceof BlockObsidian || WorldUtils.getBlock(pos.south()) instanceof BlockObsidian || WorldUtils.getBlock(pos.west()) instanceof BlockObsidian) {
                    return new SingleHole(pos, material.OBSIDIAN);
                }
                return new SingleHole(pos, material.BEDROCK);
            }
            BlockPos[] dir = new BlockPos[]{pos.west(), pos.north(), pos.east(), pos.south()};
            BlockPos pos1 = null;
            for (BlockPos dir1 : dir) {
                if (!WorldUtils.empty.contains(WorldUtils.getBlock(dir1))) continue;
                pos1 = dir1;
                break;
            }
            if (pos1 == null || WorldUtils.empty.contains(WorldUtils.getBlock(pos1.down()))) {
                return null;
            }
            BlockPos[] dir1 = new BlockPos[]{pos1.west(), pos1.north(), pos1.east(), pos1.south()};
            int checked = 0;
            boolean obi = false;
            EnumFacing facing = null;
            for (BlockPos pos2 : dir1) {
                if (pos2 == pos) continue;
                if (WorldUtils.getBlock(pos2) instanceof BlockObsidian) {
                    obi = true;
                    ++checked;
                }
                if (WorldUtils.getBlock(pos2) != Blocks.BEDROCK) continue;
                ++checked;
            }
            for (BlockPos pos2 : dir) {
                if (pos2 == pos1) continue;
                if (WorldUtils.getBlock(pos2) instanceof BlockObsidian) {
                    obi = true;
                    ++checked;
                }
                if (WorldUtils.getBlock(pos2) != Blocks.BEDROCK) continue;
                ++checked;
            }
            for (EnumFacing facing1 : EnumFacing.values()) {
                if (!pos.add(facing1.getFrontOffsetX(), facing1.getFrontOffsetY(), facing1.getFrontOffsetZ()).equals(pos1)) continue;
                facing = facing1;
            }
            if (checked >= 6) {
                return new DoubleHole(pos, pos1, obi ? material.OBSIDIAN : material.BEDROCK, facing);
            }
        }
        return null;
    }

    public static ArrayList<Hole> holes(float r, int height) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        for (BlockPos pos : WorldUtils.getSphere(PlayerUtil.getPlayerPosFloored(), r, (int)r, false, true, 0)) {
            boolean a;
            Hole hole = HoleUtil.getHole(pos, height);
            if (hole instanceof QuadHole) {
                a = false;
                for (Hole hole1 : holes) {
                    if (!(hole1 instanceof QuadHole) || !((QuadHole)hole1).contains((QuadHole)hole)) continue;
                    a = true;
                    break;
                }
                if (a) continue;
            }
            if (hole instanceof DoubleHole) {
                a = false;
                for (Hole hole1 : holes) {
                    if (!(hole1 instanceof DoubleHole) || !((DoubleHole)hole1).contains((DoubleHole)hole)) continue;
                    a = true;
                    break;
                }
                if (a) continue;
            }
            if (hole == null) continue;
            holes.add(hole);
        }
        return holes;
    }

    public static final class QuadHole
    extends Hole {
        public BlockPos pos;
        public BlockPos pos1;
        public BlockPos pos2;
        public BlockPos pos3;
        public EnumFacing dir;

        public QuadHole(BlockPos pos, BlockPos pos1, BlockPos pos2, BlockPos pos3, material mat, EnumFacing dir) {
            super(type.QUAD, mat);
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

        public boolean contains(QuadHole pos) {
            if (pos.pos.equals(this.pos) || pos.pos.equals(this.pos1) || pos.pos.equals(this.pos2)) {
                return true;
            }
            return pos.pos3.equals(this.pos) || pos.pos1.equals(this.pos3);
        }

        public boolean equals(QuadHole pos) {
            return pos.pos3.equals(this.pos) || pos.pos3.equals(this.pos3) || pos.pos2.equals(this.pos) || pos.pos2.equals(this.pos2) || pos.pos1.equals(this.pos) || pos.pos1.equals(this.pos1) && (pos.pos.equals(this.pos) || pos.pos.equals(this.pos3) || pos.pos.equals(this.pos2) || pos.pos.equals(this.pos1));
        }
    }

    public static final class DoubleHole
    extends Hole {
        public BlockPos pos;
        public BlockPos pos1;
        public EnumFacing dir;

        public DoubleHole(BlockPos pos, BlockPos pos1, material mat, EnumFacing dir) {
            super(type.DOUBLE, mat);
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

        public boolean contains(DoubleHole pos) {
            if (pos.pos.equals(this.pos) || pos.pos.equals(this.pos1)) {
                return true;
            }
            return pos.pos1.equals(this.pos) || pos.pos1.equals(this.pos1);
        }

        public boolean equals(DoubleHole pos) {
            return !(!pos.pos1.equals(this.pos) && !pos.pos1.equals(this.pos1) || !pos.pos.equals(this.pos) && !pos.pos.equals(this.pos1));
        }
    }

    public static final class SingleHole
    extends Hole {
        public BlockPos pos;

        public SingleHole(BlockPos pos, material mat) {
            super(type.SINGLE, mat);
            this.pos = pos;
        }
    }

    public static class Hole {
        public type type;
        public material mat;

        public Hole(type type2, material mat) {
            this.type = type2;
            this.mat = mat;
        }
    }

    public static enum material {
        BEDROCK,
        OBSIDIAN;

    }

    public static enum type {
        DOUBLE,
        SINGLE,
        QUAD;

    }
}
