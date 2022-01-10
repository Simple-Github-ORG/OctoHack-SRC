package me.primooctopus33.octohack.mixin.mixins;

import com.google.common.base.Predicate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.player.NoEntityTrace;
import me.primooctopus33.octohack.client.modules.render.Ambience;
import me.primooctopus33.octohack.client.modules.render.NoRender;
import me.primooctopus33.octohack.event.events.PerspectiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class}, priority=1001)
public class MixinEntityRenderer {
    @Shadow
    public float field_78530_s;
    @Shadow
    public double field_78503_V;
    @Shadow
    public double field_78502_W;
    @Shadow
    public double field_78509_X;
    @Shadow
    public int field_175084_ae;
    @Shadow
    @Final
    private int[] field_78504_Q;
    @Shadow
    public int field_175079_V;
    @Shadow
    public int field_78529_t;
    @Shadow
    public boolean field_175078_W;
    Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    public void orientCamera(float partialTicks) {
    }

    @Shadow
    public void hurtCameraEffect(float partialTicks) {
    }

    @Shadow
    public void setupViewBobbing(float partialTicks) {
    }

    @Shadow
    public void enableLightmap() {
    }

    @Shadow
    public void disableLightmap() {
    }

    @Shadow
    public void updateFogColor (float partialTicks) {
    }

    @Shadow
    public void updateFogColor(int startCoords, float partialTicks) {
    }

    protected MixinEntityRenderer(RenderManager renderManager) {
    }

    @Inject(method={"updateLightmap"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ENTITY || NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ALL)) {
            info.cancel();
        }
    }

    @Inject(method={"updateLightmap"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift=At.Shift.BEFORE)})
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        try {
            Ambience ambience = OctoHack.moduleManager.getModuleByClass(Ambience.class);
            if (ambience.isEnabled()) {
                for (int i = 0; i < this.field_78504_Q.length; ++i) {
                    Color ambientColor = new Color(ambience.r.getValue(), ambience.g.getValue(), ambience.b.getValue(), ambience.a.getValue());
                    int alpha = ambientColor.getAlpha();
                    float modifier = (float)alpha / 255.0f;
                    int color = this.field_78504_Q[i];
                    int[] bgr = this.toRGBAArray(color);
                    Vector3f values = new Vector3f((float)bgr[2] / 255.0f, (float)bgr[1] / 255.0f, (float)bgr[0] / 255.0f);
                    Vector3f newValues = new Vector3f((float)ambientColor.getRed() / 255.0f, (float)ambientColor.getGreen() / 255.0f, (float)ambientColor.getBlue() / 255.0f);
                    Vector3f finalValues = this.mix(values, newValues, modifier);
                    int red = (int)(finalValues.x * 255.0f);
                    int green = (int)(finalValues.y * 255.0f);
                    int blue = (int)(finalValues.z * 255.0f);
                    this.field_78504_Q[i] = 0xFF000000 | red << 16 | green << 8 | blue;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().hurtCam.getValue().booleanValue() && NoRender.getInstance().isOn()) {
            info.cancel();
        }
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        if (NoEntityTrace.getINSTANCE().isOn() && (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && NoEntityTrace.getINSTANCE().pickaxe.getValue() != false || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && NoEntityTrace.getINSTANCE().crystal.getValue() != false || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && NoEntityTrace.getINSTANCE().gapple.getValue() != false || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.TNT_MINECART)) {
            return new ArrayList<Entity>();
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
}
