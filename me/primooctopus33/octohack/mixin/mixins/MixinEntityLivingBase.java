package me.primooctopus33.octohack.mixin.mixins;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.render.Animations;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityLivingBase.class})
public class MixinEntityLivingBase {
    @Inject(method={"getArmSwingAnimationEnd"}, at={@At(value="HEAD")}, cancellable=true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {
        if (OctoHack.moduleManager.isModuleEnabled("Animations") && Animations.changeSwing.getValue().booleanValue()) {
            info.setReturnValue((int)Animations.swingDelay.getValue());
        }
    }
}
