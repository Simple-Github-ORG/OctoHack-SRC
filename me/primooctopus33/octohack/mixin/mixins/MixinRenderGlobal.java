package me.primooctopus33.octohack.mixin.mixins;

import me.primooctopus33.octohack.client.modules.movement.GroundSpeed;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderGlobal.class})
public abstract class MixinRenderGlobal {
    @Redirect(method={"setupTerrain"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ChunkRenderContainer;initialize(DDD)V"))
    public void initializeHook(ChunkRenderContainer chunkRenderContainer, double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
        double y = viewEntityYIn;
        if (GroundSpeed.getInstance().isOn() && GroundSpeed.getInstance().noShake.getValue().booleanValue() && GroundSpeed.getInstance().mode.getValue() != GroundSpeed.Mode.INSTANT && GroundSpeed.getInstance().antiShake) {
            y = GroundSpeed.getInstance().startY;
        }
        chunkRenderContainer.initialize(viewEntityXIn, y, viewEntityZIn);
    }

    @Redirect(method={"renderEntities"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/RenderManager;setRenderPosition(DDD)V"))
    public void setRenderPositionHook(RenderManager renderManager, double renderPosXIn, double renderPosYIn, double renderPosZIn) {
        double y = renderPosYIn;
        if (GroundSpeed.getInstance().isOn() && GroundSpeed.getInstance().noShake.getValue().booleanValue() && GroundSpeed.getInstance().mode.getValue() != GroundSpeed.Mode.INSTANT && GroundSpeed.getInstance().antiShake) {
            y = GroundSpeed.getInstance().startY;
        }
        TileEntityRendererDispatcher.staticPlayerY = y;
        renderManager.setRenderPosition(renderPosXIn, TileEntityRendererDispatcher.staticPlayerY, renderPosZIn);
    }

    @Redirect(method={"drawSelectionBox"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/math/AxisAlignedBB;"))
    public AxisAlignedBB offsetHook(AxisAlignedBB axisAlignedBB, double x, double y, double z) {
        double yIn = y;
        if (GroundSpeed.getInstance().isOn() && GroundSpeed.getInstance().noShake.getValue().booleanValue() && GroundSpeed.getInstance().mode.getValue() != GroundSpeed.Mode.INSTANT && GroundSpeed.getInstance().antiShake) {
            yIn = GroundSpeed.getInstance().startY;
        }
        return axisAlignedBB.offset(x, y, z);
    }
}
