package me.primooctopus33.octohack.mixin.mixins.accessors;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderEnderCrystal.class})
public interface IRenderEnderCrystal {
    @Accessor(value="modelEnderCrystalNoBase")
    public ModelBase getModelEnderCrystalNoBase();

    @Accessor(value="modelEnderCrystalNoBase")
    public void setModelEnderCrystalNoBase(ModelBase var1);
}
