package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.RenderEntityEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrystalChams
extends Module {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/enchanted_item_glint.png");
    public static Setting<Integer> crystalRotateSpeed;
    public static Setting<Integer> crystalScale;
    public static Setting<Integer> crystalBounce;
    public static Setting<Integer> crystalFillr;
    public static Setting<Integer> crystalFillg;
    public static Setting<Integer> crystalFillb;
    public static Setting<Integer> crystalFilla;
    public static Setting<Integer> crystalLiner;
    public static Setting<Integer> crystalLineg;
    public static Setting<Integer> crystalLineb;
    public static Setting<Integer> crystalLinea;
    public static Setting<Boolean> crystalGlint;
    public static Setting<Integer> crystalGlintr;
    public static Setting<Integer> crystalGlintg;
    public static Setting<Integer> crystalGlintb;
    public static Setting<Integer> crystalGlinta;
    public static Setting<Double> lineWidth;
    public static Setting<Double> lineWidthInterp;
    public static Setting<Boolean> customBlendFunc;

    public CrystalChams() {
        super("CrystalChams", "Allows you to modify how end crystals look", Module.Category.RENDER, true, false, false);
        crystalRotateSpeed = this.register(new Setting<Integer>("Crystal Spin Speed", 1, 0, 10));
        crystalScale = this.register(new Setting<Integer>("Crystal Scale", 1, 0, 10));
        crystalFillr = this.register(new Setting<Integer>("Crystal Fill Red", 0, 0, 255));
        crystalFillg = this.register(new Setting<Integer>("Crystal Fill Green", 0, 0, 255));
        crystalFillb = this.register(new Setting<Integer>("Crystal Fill Blue", 0, 0, 255));
        crystalFilla = this.register(new Setting<Integer>("Crystal Fill Alpha", 0, 0, 255));
        crystalLiner = this.register(new Setting<Integer>("Crystal Line Red", 0, 0, 255));
        crystalLineg = this.register(new Setting<Integer>("Crystal Line Green", 0, 0, 255));
        crystalLineb = this.register(new Setting<Integer>("Crystal Line Blue", 0, 0, 255));
        crystalLinea = this.register(new Setting<Integer>("Crystal Line Alpha", 0, 0, 255));
        crystalGlint = this.register(new Setting<Boolean>("Crystal Glint", false));
        crystalGlintr = this.register(new Setting<Integer>("Crystal Glint Red", 0, 0, 255));
        crystalGlintg = this.register(new Setting<Integer>("Crystal Glint Green", 0, 0, 255));
        crystalGlintb = this.register(new Setting<Integer>("Crystal Glint Blue", 0, 0, 255));
        crystalGlinta = this.register(new Setting<Integer>("Crystal Glint Alpha", 0, 0, 255));
        lineWidth = this.register(new Setting<Double>("Line Width", 1.0, 0.1, 5.0));
        lineWidthInterp = this.register(new Setting<Double>("Line Width Interp", 5.0, 0.1, 15.0));
        customBlendFunc = this.register(new Setting<Boolean>("Custom Blend", true));
    }

    public Color getCrystalFill() {
        return new Color(crystalFillr.getValue(), crystalFillg.getValue(), crystalFillb.getValue(), crystalFilla.getValue());
    }

    public Color getCrystalLine() {
        return new Color(crystalLiner.getValue(), crystalLineg.getValue(), crystalLineb.getValue(), crystalLinea.getValue());
    }

    public Color getCrystalGlint() {
        return new Color(crystalGlintr.getValue(), crystalGlintg.getValue(), crystalGlintb.getValue(), crystalGlinta.getValue());
    }

    @SubscribeEvent
    public void onRenderModel(RenderEntityEvent event) {
        float scale;
        float limbSwingAmt;
        boolean nullCheck = CrystalChams.mc.player == null || CrystalChams.mc.world == null || event.entityIn == null;
        RenderUtil.prepare();
        GL11.glPushAttrib(1048575);
        if (customBlendFunc.getValue().booleanValue()) {
            GL11.glBlendFunc(770, 32772);
        }
        GL11.glEnable(2881);
        GL11.glEnable(2848);
        Color line = this.getCrystalLine();
        Color fill = this.getCrystalFill();
        boolean texture = crystalGlint.getValue();
        Color textureColor = this.getCrystalGlint();
        float f = event.entityIn instanceof EntityEnderCrystal ? event.limbSwingAmount * ((Number)crystalRotateSpeed.getValue()).floatValue() : (limbSwingAmt = event.limbSwingAmount);
        float f2 = event.entityIn instanceof EntityEnderCrystal ? ((Number)crystalScale.getValue()).floatValue() : (scale = event.scale);
        GlStateManager.glLineWidth((float)(nullCheck ? ((Number)lineWidth.getValue()).floatValue() : RenderUtil.getInterpolatedLinWid(CrystalChams.mc.player.getDistance(event.entityIn) + 1.0f, ((Number)lineWidth.getValue()).floatValue(), ((Number)lineWidthInterp.getValue()).floatValue())));
        GlStateManager.disableAlpha();
        if (texture) {
            mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
            GL11.glTexCoord3d(1.0, 1.0, 1.0);
            GL11.glEnable(3553);
            GL11.glBlendFunc(768, 771);
            ColorUtil.glColor(textureColor);
            event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            if (customBlendFunc.getValue().booleanValue()) {
                GL11.glBlendFunc(770, 32772);
            } else {
                GL11.glBlendFunc(770, 771);
            }
        }
        ColorUtil.glColor(fill);
        GL11.glDisable(3553);
        event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        GL11.glPolygonMode(1032, 6913);
        ColorUtil.glColor(line);
        event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        GL11.glPolygonMode(1032, 6914);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popAttrib();
        RenderUtil.release();
        event.setCanceled(true);
    }
}
