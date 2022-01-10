package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

public class RenderEvent
extends EventStage {
    private final Tessellator tessellator;
    private final Vec3d renderPos;

    public RenderEvent(Tessellator tessellator, Vec3d renderPos) {
        this.tessellator = tessellator;
        this.renderPos = renderPos;
    }

    public Tessellator getTessellator() {
        return this.tessellator;
    }

    public BufferBuilder getBuffer() {
        return this.tessellator.getBuffer();
    }

    public Vec3d getRenderPos() {
        return this.renderPos;
    }

    public void setTranslation(Vec3d translation) {
        this.getBuffer().setTranslation(-translation.x, -translation.y, -translation.z);
    }

    public void resetTranslation() {
        this.setTranslation(this.renderPos);
    }
}
