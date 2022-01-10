package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.util.BlockUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoCrystal$PlaceInfo {
    private final BlockPos pos;
    private final boolean offhand;
    private final boolean placeSwing;
    private final boolean exactHand;
    private final boolean silent;

    public AutoCrystal$PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand, boolean silent) {
        this.pos = pos;
        this.offhand = offhand;
        this.placeSwing = placeSwing;
        this.exactHand = exactHand;
        this.silent = silent;
    }

    public void runPlace() {
        BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand, this.silent);
    }
}
