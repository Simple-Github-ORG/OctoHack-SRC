package me.primooctopus33.octohack.client.modules.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.math.BlockPos;

public class BedAura$BedData {
    private final BlockPos pos;
    private final IBlockState state;
    private final boolean isHeadPiece;
    private final TileEntityBed entity;

    public BedAura$BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
        this.pos = pos;
        this.state = state;
        this.entity = bed;
        this.isHeadPiece = isHeadPiece;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public IBlockState getState() {
        return this.state;
    }

    public boolean isHeadPiece() {
        return this.isHeadPiece;
    }

    public TileEntityBed getEntity() {
        return this.entity;
    }
}
