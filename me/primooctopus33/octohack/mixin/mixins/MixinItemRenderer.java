package me.primooctopus33.octohack.mixin.mixins;

import me.primooctopus33.octohack.client.modules.render.HandView;
import me.primooctopus33.octohack.client.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    @Shadow
    @Final
    public Minecraft field_78455_a;
    private boolean injection = true;

    @Shadow
    public abstract void func_187457_a(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Shadow
    protected abstract void func_187456_a(float var1, float var2, EnumHandSide var3);

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            HandView offset = HandView.getINSTANCE();
            float xOffset = 0.0f;
            float yOffset = 0.0f;
            this.injection = false;
            if (hand == EnumHand.MAIN_HAND) {
                if (offset.isOn() && player.func_184614_ca() != ItemStack.EMPTY) {
                    xOffset = offset.mainX.getValue().floatValue();
                    yOffset = offset.mainY.getValue().floatValue();
                }
            } else if (!offset.normalOffset.getValue().booleanValue() && offset.isOn() && player.func_184592_cb() != ItemStack.EMPTY) {
                xOffset = offset.offX.getValue().floatValue();
                yOffset = offset.offY.getValue().floatValue();
            }
            this.func_187457_a(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
            this.injection = true;
        }
    }

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fire.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Redirect(method={"renderArmFirstPerson"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal=0))
    public void translateHook(float x, float y, float z) {
        HandView offset = HandView.getINSTANCE();
        boolean shiftPos = Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getHeldItemMainhand() != ItemStack.EMPTY && offset.isOn();
        GlStateManager.translate((float)(x + (shiftPos ? offset.mainX.getValue().floatValue() : 0.0f)), (float)(y + (shiftPos ? offset.mainY.getValue().floatValue() : 0.0f)), (float)z);
    }
}
