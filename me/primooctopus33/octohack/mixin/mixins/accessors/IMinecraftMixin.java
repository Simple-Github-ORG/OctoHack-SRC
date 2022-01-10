package me.primooctopus33.octohack.mixin.mixins.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Minecraft.class})
public interface IMinecraftMixin {
    @Accessor(value="rightClickDelayTimer")
    public void setRightClickDelayTimerAccessor(int var1);
}
