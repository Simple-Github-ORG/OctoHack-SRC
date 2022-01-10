package me.primooctopus33.octohack.mixin.mixins;

import java.awt.Color;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.render.HandView;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderItem.class})
public class MixinRenderItem {
    @Shadow
    private void func_191967_a(IBakedModel model, int color, ItemStack stack) {
    }

    @Redirect(method={"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void yes(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        if (OctoHack.moduleManager.isModuleEnabled("HandView")) {
            this.func_191967_a(model, new Color(1.0f, 1.0f, 1.0f, (float)HandView.getINSTANCE().viewAlpha.getValue().intValue() / 255.0f).getRGB(), stack);
        } else {
            this.func_191967_a(model, -1, stack);
        }
    }

    @Redirect(method={"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    private void renderItem(float colorRed, float colorGreen, float colorBlue, float alpha) {
        if (OctoHack.moduleManager.isModuleEnabled("HandView")) {
            GlStateManager.color((float)colorRed, (float)colorGreen, (float)colorBlue, (float)((float)HandView.getINSTANCE().viewAlpha.getValue().intValue() / 255.0f));
        } else {
            GlStateManager.color((float)colorRed, (float)colorGreen, (float)colorBlue, (float)alpha);
        }
    }
}
