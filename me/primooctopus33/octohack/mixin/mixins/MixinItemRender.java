package me.primooctopus33.octohack.mixin.mixins;

import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.modules.render.HandView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderItem.class})
public abstract class MixinItemRender {
    @Inject(method={"renderItemModel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift=At.Shift.BEFORE)})
    private void test(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (((Boolean)HandView.getINSTANCE().enabled.getValue()).booleanValue() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Feature.fullNullCheck()) {
            GlStateManager.scale((float)HandView.getINSTANCE().sizeX.getValue().floatValue(), (float)HandView.getINSTANCE().sizeY.getValue().floatValue(), (float)HandView.getINSTANCE().sizeZ.getValue().floatValue());
            GlStateManager.rotate((float)(HandView.getINSTANCE().rotationX.getValue().floatValue() * 360.0f), (float)1.0f, (float)0.0f, (float)0.0f);
            GlStateManager.rotate((float)(HandView.getINSTANCE().rotationY.getValue().floatValue() * 360.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)(HandView.getINSTANCE().rotationZ.getValue().floatValue() * 360.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.translate((float)HandView.getINSTANCE().positionX.getValue().floatValue(), (float)HandView.getINSTANCE().positionY.getValue().floatValue(), (float)HandView.getINSTANCE().positionZ.getValue().floatValue());
        }
    }
}
