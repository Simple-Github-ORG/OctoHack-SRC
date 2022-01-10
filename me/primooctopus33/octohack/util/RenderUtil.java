package me.primooctopus33.octohack.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.mixin.mixins.accessors.IRenderManager;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class RenderUtil
implements Util {
    private static final Frustum frustrum = new Frustum();
    private static final FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    private static final IntBuffer viewport = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
    public static RenderItem itemRender = RenderUtil.mc.getItemRenderer().itemRenderer;
    public static ICamera camera = new Frustum();
    private static boolean depth = GL11.glIsEnabled(2896);
    private static boolean texture = GL11.glIsEnabled(3042);
    private static boolean clean = GL11.glIsEnabled(3553);
    private static boolean bind = GL11.glIsEnabled(2929);
    private static boolean override = GL11.glIsEnabled(2848);
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder builder;
    private static final BufferBuilder BufferBuilder;
    public static int deltaTime;

    public static void gradientBox(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean speedmine) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            double dist = RenderUtil.mc.player.getDistance((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f) * 0.75;
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawRectangleCorrectly(int x, int y, int w, int h, int color) {
        GL11.glLineWidth(1.0f);
        Gui.drawRect((int)x, (int)y, (int)(x + w), (int)(y + h), (int)color);
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return RenderUtil.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static Vec3d to2D(double x, double y, double z) {
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new Vec3d(screenCoords.get(0), (float)Display.getHeight() - screenCoords.get(1), screenCoords.get(2));
        }
        return null;
    }

    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
        boolean blend = GL11.glIsEnabled(3042);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        RenderUtil.hexColor(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x - size / widthDiv, y + size);
        GL11.glVertex2d(x, y + size / heightDiv);
        GL11.glVertex2d(x + size / widthDiv, y + size);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        if (outline) {
            GL11.glLineWidth(outlineWidth);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, alpha);
            GL11.glBegin(2);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d(x - size / widthDiv, y + size);
            GL11.glVertex2d(x, y + size / heightDiv);
            GL11.glVertex2d(x + size / widthDiv, y + size);
            GL11.glVertex2d(x, y);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        if (!blend) {
            GL11.glDisable(3042);
        }
        GL11.glDisable(2848);
    }

    public static void hexColor(int hexColor) {
        float red = (float)(hexColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(hexColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hexColor & 0xFF) / 255.0f;
        float alpha = (float)(hexColor >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x + 0, y + height, zLevel).tex((float)(textureX + 0) * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + 0, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)(textureY + 0) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + 0, y + 0, zLevel).tex((float)(textureX + 0) * 0.00390625f, (float)(textureY + 0) * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public static void blockESP(BlockPos b, Color c, double length, double length2) {
        RenderUtil.blockEsp(b, c, length, length2);
    }

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
        GL11.glScissor((int)(x * (float)sr.getScaleFactor()), (int)((float)RenderUtil.mc.displayHeight - y1 * (float)sr.getScaleFactor()), (int)((x1 - x) * (float)sr.getScaleFactor()), (int)((y1 - y) * (float)sr.getScaleFactor()));
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawBox(BlockPos pos, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            assert (RenderUtil.mc.renderViewEntity != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.renderViewEntity, mc.getRenderPartialTicks());
            RenderUtil.drawBlockOutline(iblockstate.func_185918_c(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z), color, linewidth);
        }
    }

    public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static double getRenderPosX() {
        return ((IRenderManager)((Object)mc.getRenderManager())).getRenderPosX();
    }

    public static double getRenderPosY() {
        return ((IRenderManager)((Object)mc.getRenderManager())).getRenderPosY();
    }

    public static double getRenderPosZ() {
        return ((IRenderManager)((Object)mc.getRenderManager())).getRenderPosZ();
    }

    public static void drawBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            double dist = RenderUtil.mc.player.getDistance((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f) * 0.75;
            if (box) {
                RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)boxAlpha / 255.0f));
            }
            if (outline) {
                RenderGlobal.drawBoundingBox((double)bb.minX, (double)bb.minY, (double)bb.minZ, (double)bb.maxX, (double)bb.maxY, (double)bb.maxZ, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawOutlineRect(float x, float y, float w, float h, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth((float)1.0f);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void start2() {
        GlStateManager.pushMatrix();
        RenderUtil.width(1.0f);
        GL11.glEnable(2848);
        GL11.glEnable(34383);
        GL11.glHint(3154, 4354);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel((int)7425);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
    }

    public static void end2() {
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.enableAlpha();
        GlStateManager.depthMask((boolean)true);
        GL11.glDisable(34383);
        GL11.glDisable(2848);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
        RenderUtil.width(1.0f);
        GlStateManager.popMatrix();
    }

    private static void vertex(double x, double y, double z, int r, int g, int b, int a) {
        builder.pos(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ).color(r, g, b, a).endVertex();
    }

    private static void vertex(int r, int g, int b, int a) {
        builder.pos(0.0, RenderUtil.mc.player.getEyeHeight(), 0.0).color(r, g, b, a).endVertex();
    }

    private static BufferBuilder vertex(double x, double y, double z) {
        return builder.pos(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void start1() {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
    }

    private static void width(float width) {
        GlStateManager.glLineWidth((float)width);
    }

    public static void drawBBClaw(AxisAlignedBB bb, float width, float height, Color color) {
        GlStateManager.pushMatrix();
        RenderUtil.start1();
        RenderUtil.width(width);
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY;
        double maxZ = bb.maxZ;
        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil.vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, minY, minZ + (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, minY, maxZ - (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, minY, minZ + (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ - (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX + (double)height, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX + (double)height, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX - (double)height, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX - (double)height, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, minY + (double)height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, minY + (double)height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, minY + (double)height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, minY + (double)height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, maxY, minZ + (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ - (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ + (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ - (double)height).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX + (double)height, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX + (double)height, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX - (double)height, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX - (double)height, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, maxY - (double)height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(minX, maxY - (double)height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, maxY - (double)height, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0f).endVertex();
        RenderUtil.vertex(maxX, maxY - (double)height, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        RenderUtil.end1();
        GlStateManager.popMatrix();
    }

    public static void drawBBFill(AxisAlignedBB bb, Color color) {
        GlStateManager.pushMatrix();
        RenderUtil.start1();
        RenderUtil.width(1.0f);
        int r = color.getRed();
        int b = color.getBlue();
        int g = color.getGreen();
        int a = color.getAlpha();
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY;
        double maxZ = bb.maxZ;
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        RenderUtil.end1();
        GlStateManager.popMatrix();
    }

    public static void drawBBSlab(AxisAlignedBB bb, float height, Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX;
        double maxY = bb.maxY + (double)height;
        double maxZ = bb.maxZ;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GL11.glDisable(2929);
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.disableCull();
        GlStateManager.shadeModel((int)7425);
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(maxX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        RenderUtil.vertex(minX, maxY, maxZ).color(0, 0, 0, 0).endVertex();
        RenderUtil.vertex(minX, maxY, minZ).color(0, 0, 0, 0).endVertex();
        tessellator.draw();
        GL11.glEnable(2929);
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawBBOutline(AxisAlignedBB bb, float width, Color color) {
        RenderUtil.start(width);
        int r = color.getRed();
        int b = color.getBlue();
        int g = color.getGreen();
        int a = color.getAlpha();
        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil.vertex(bb.minX, bb.minY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.minY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.minY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.minY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.maxY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.minY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.maxY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.minY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.maxX, bb.maxY, bb.minZ, r, g, b, a);
        RenderUtil.vertex(bb.minX, bb.maxY, bb.minZ, r, g, b, a);
        tessellator.draw();
        RenderUtil.end();
    }

    public static void end() {
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(2884);
        GlStateManager.popMatrix();
    }

    public static void start(float width) {
        GlStateManager.pushMatrix();
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderUtil.width(width);
    }

    public static void end1() {
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth((float)1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawBox(BlockPos blockPos, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawBox(double x, double y, double z, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.drawBox(tessellator.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBoxSmall(double x, double y, double z, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.drawBox(tessellator.getBuffer(), x, y, z, 0.25f, 0.25f, 0.25f, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        RenderUtil.drawBox(tessellator.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static BufferBuilder getBufferBuilder() {
        return tessellator.getBuffer();
    }

    public static void drawBox(BufferBuilder buffer, double x, double y, double z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(x + (double)w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 2) != 0) {
            buffer.pos(x + (double)w, y + (double)h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
        }
        if ((sides & 4) != 0) {
            buffer.pos(x + (double)w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y + (double)h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 8) != 0) {
            buffer.pos(x, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + (double)h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0) {
            buffer.pos(x + (double)w, y, z + (double)d).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y + (double)h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + (double)w, y + (double)h, z + (double)d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 0x11) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x12) != 0) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x21) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x22) != 0) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 5) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 6) != 0) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 9) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0xA) != 0) {
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x14) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x24) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x18) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x28) != 0) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawRectangle(float x, float y, float w, float h, int color) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBoundingBoxBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosXInMiddle(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosXInMiddle2(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosSouth(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosNorth(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosEast(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPosWest(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha / 2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBlockPosHalf(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 0.5, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBottomBoxBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 0.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxChestBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double)bp.getX() + 0.06 - mc.getRenderManager().viewerPosX;
        double y = (double)bp.getY() - mc.getRenderManager().viewerPosY;
        double z = (double)bp.getZ() + 0.06 - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 0.881, y + 0.875, z + 0.881);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxItem(double xloc, double yloc, double zloc, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = xloc - 0.2 - mc.getRenderManager().viewerPosX;
        double y = yloc - mc.getRenderManager().viewerPosY;
        double z = zloc - 0.2 - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 0.4, y + 0.4, z + 0.4);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxFace(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoxBottom(BufferBuilder buffer, float x, float y, float z, float w, float d, int r, int g, int b, int a) {
        buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x, y, z).color(r, g, b, a).endVertex();
    }

    public static void prepare(int mode2) {
        RenderUtil.prepareGL();
        RenderUtil.begin(mode2);
    }

    public static void prepare(String mode_requested) {
        int mode = 0;
        if (mode_requested.equalsIgnoreCase("quads")) {
            mode = 7;
        } else if (mode_requested.equalsIgnoreCase("lines")) {
            mode = 1;
        }
        RenderUtil.prepare_gl();
        RenderUtil.begin(mode);
    }

    public static void prepare_gl() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth((float)1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glLineWidth(2.0f);
    }

    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, double height, boolean gradient, boolean invert, int alpha) {
        if (gradient) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawGradientBlockOutline(pos, invert ? endColor : color, invert ? color : endColor, linewidth, height);
            return;
        }
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            AxisAlignedBB blockAxis = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
            RenderUtil.drawBlockOutline(blockAxis.grow(0.002f), color, linewidth);
        }
    }

    public static void drawGradientBlockOutline(BlockPos pos, Color startColor, Color endColor, float linewidth, double height) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        RenderUtil.drawGradientBlockOutline(iblockstate.func_185918_c(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, height, 0.0), startColor, endColor, linewidth);
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red2 = (float)endColor.getRed() / 255.0f;
        float green2 = (float)endColor.getGreen() / 255.0f;
        float blue2 = (float)endColor.getBlue() / 255.0f;
        float alpha2 = (float)endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, gradientOutline, invertGradientOutline, gradientAlpha);
        }
    }

    public static void drawOpenGradientBox(BlockPos pos, Color startColor, Color endColor, double height) {
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP) continue;
            RenderUtil.drawGradientPlane(pos, face, startColor, endColor, height);
        }
    }

    public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, double height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB bb = iblockstate.func_185918_c(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, height, 0.0);
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red2 = (float)endColor.getRed() / 255.0f;
        float green2 = (float)endColor.getGreen() / 255.0f;
        float blue2 = (float)endColor.getBlue() / 255.0f;
        float alpha2 = (float)endColor.getAlpha() / 255.0f;
        double x1 = 0.0;
        double y1 = 0.0;
        double z1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double z2 = 0.0;
        if (face == EnumFacing.DOWN) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.minY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.UP) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.maxY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.EAST) {
            x1 = bb.maxX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.WEST) {
            x1 = bb.minX;
            x2 = bb.minX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.SOUTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.maxZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.NORTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.minZ;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask((boolean)false);
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.UP) {
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.DOWN) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, boolean half, boolean top) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z);
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red2 = (float)endColor.getRed() / 255.0f;
        float green2 = (float)endColor.getGreen() / 255.0f;
        float blue2 = (float)endColor.getBlue() / 255.0f;
        float alpha2 = (float)endColor.getAlpha() / 255.0f;
        double x1 = 0.0;
        double y1 = 0.0;
        double z1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double z2 = 0.0;
        if (face == EnumFacing.DOWN) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.minY + (top ? 0.5 : 0.0);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.UP) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.maxY / (double)(half ? 2 : 1);
            y2 = bb.maxY / (double)(half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.EAST) {
            x1 = bb.maxX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double)(half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.WEST) {
            x1 = bb.minX;
            x2 = bb.minX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double)(half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.SOUTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double)(half ? 2 : 1);
            z1 = bb.maxZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.NORTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double)(half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.minZ;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask((boolean)false);
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.UP) {
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.DOWN) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawBox(BlockPos pos, Color color, double height, boolean gradient, boolean invert, int alpha) {
        if (gradient) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawOpenGradientBox(pos, invert ? endColor : color, invert ? color : endColor, height);
            return;
        }
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void render() {
        tessellator.draw();
    }

    public static void release_gl() {
        GlStateManager.enableCull();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void draw_cube(BlockPos blockPos, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.draw_cube(blockPos, r, g, b, a, sides);
    }

    public static void draw_cube(float x, float y, float z, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.draw_cube(tessellator.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void draw_cube(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        RenderUtil.draw_cube(tessellator.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void draw_cube_line(BlockPos blockPos, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.draw_cube_line(blockPos, r, g, b, a, sides);
    }

    public static void draw_cube_line(float x, float y, float z, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        RenderUtil.draw_cube_line(tessellator.getBuffer(), x, y, z, 1.0f, 0.5645f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBordered(float x, float y, float x2, float y2, float thickness, int inside, int outline) {
        float fix = 0.0f;
        if (thickness < 1.0f) {
            fix = 1.0f;
        }
        RenderUtil.drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
        RenderUtil.drawRect(x, y + 1.0f - fix, x + thickness, y2, outline);
        RenderUtil.drawRect(x, y, x2 - 1.0f + fix, y + thickness, outline);
        RenderUtil.drawRect(x2 - thickness, y, x2, y2 - 1.0f + fix, outline);
        RenderUtil.drawRect(x + 1.0f - fix, y2 - thickness, x2, y2, outline);
    }

    public static void draw_cube_line(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        RenderUtil.draw_cube_line(tessellator.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static BufferBuilder get_buffer_build() {
        return tessellator.getBuffer();
    }

    public static void draw_cube(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (Arrays.asList(sides.split("-")).contains("down") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("up") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("north") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("south") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void draw_cube_line(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (Arrays.asList(sides.split("-")).contains("downwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("nortwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("norteast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("southweast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("southeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void begin(int mode2) {
        tessellator.getBuffer().begin(mode2, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void drawBoxBottom(BlockPos bp, int r, int g, int b, int a) {
        RenderUtil.prepare(7);
        RenderUtil.drawBoxBottom(tessellator.getBuffer(), bp.getX(), bp.getY(), bp.getZ(), 1.0f, 1.0f, r, g, b, a);
        RenderUtil.release();
    }

    public static void drawFace(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(RenderUtil.getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return RenderUtil.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        RenderUtil.enableGL2D();
        RenderUtil.fakeGuiRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        RenderUtil.fakeGuiRect(x + width, y, x1 - width, y + width, borderColor);
        RenderUtil.fakeGuiRect(x, y, x + width, y1, borderColor);
        RenderUtil.fakeGuiRect(x1 - width, y, x1, y1, borderColor);
        RenderUtil.fakeGuiRect(x + width, y1 - width, x1 - width, y1, borderColor);
        RenderUtil.disableGL2D();
    }

    public static AxisAlignedBB fixBB(AxisAlignedBB axisAlignedBB) {
        AxisAlignedBB bb = axisAlignedBB;
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void fakeGuiRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    private static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void renderBB(int glMode, AxisAlignedBB bb, Color bottom, Color top) {
        GL11.glShadeModel(7425);
        bb = RenderUtil.updateToCamera(bb);
        RenderUtil.prepare();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        builder = tessellator.getBuffer();
        builder.begin(glMode, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil.buildBBBuffer(builder, bb, bottom, top);
        tessellator.draw();
        RenderUtil.release();
        GL11.glShadeModel(7424);
    }

    public static void renderBBFog(AxisAlignedBB bb, Color main, Color center) {
        GL11.glShadeModel(7425);
        bb = RenderUtil.updateToCamera(bb);
        RenderUtil.prepare();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        builder = tessellator.getBuffer();
        builder.begin(4, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil.buildBBBufferFog(builder, bb, main, center);
        tessellator.draw();
        RenderUtil.release();
        GL11.glShadeModel(7424);
    }

    public static void buildBBBufferFog(BufferBuilder builder, AxisAlignedBB bb, Color main, Color center) {
        double centerX = (bb.maxX - bb.minX) / 2.0;
        double centerY = (bb.maxY - bb.minY) / 2.0;
        double centerZ = (bb.maxZ - bb.minZ) / 2.0;
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        RenderUtil.addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
    }

    public static AxisAlignedBB updateToCamera(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void addBuilderVertex(BufferBuilder bufferBuilder, double x, double y, double z, Color color) {
        bufferBuilder.pos(x, y, z).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
    }

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GL11.glShadeModel(7425);
    }

    public static void release() {
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void buildBBBuffer(BufferBuilder builder, AxisAlignedBB bb, Color bottom, Color top) {
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        RenderUtil.addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        RenderUtil.addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
    }

    public static float getInterpolatedLinWid(float distance, float line, float lineFactor) {
        return line * lineFactor / distance;
    }

    public static void drawText(BlockPos pos, String text) {
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f, RenderUtil.mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate((double)(-((double)OctoHack.textManager.getStringWidth(text) / 2.0)), (double)0.0, (double)0.0);
        OctoHack.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);
        GlStateManager.popMatrix();
    }

    public static void drawBetterBoxESP(AxisAlignedBB a, Color boxColor, Color outlineColor, float lineWidth, boolean outline, boolean box, boolean colorSync, float alpha, float scale, float slab) {
        double f = 0.5 * (double)(1.0f - scale);
        AxisAlignedBB bb = RenderUtil.interpolateAxis(new AxisAlignedBB(a.minX + f, a.minY + f + (double)(1.0f - slab), a.minZ + f, a.maxX - f, a.maxY - f, a.maxZ - f));
        float rB = (float)boxColor.getRed() / 255.0f;
        float gB = (float)boxColor.getGreen() / 255.0f;
        float bB = (float)boxColor.getBlue() / 255.0f;
        float aB = (float)boxColor.getAlpha() / 255.0f;
        float rO = (float)outlineColor.getRed() / 255.0f;
        float gO = (float)outlineColor.getGreen() / 255.0f;
        float bO = (float)outlineColor.getBlue() / 255.0f;
        float aO = (float)outlineColor.getAlpha() / 255.0f;
        if (colorSync) {
            // empty if block
        }
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        aB *= alpha;
        aO *= alpha;
        if (box) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)rB, (float)gB, (float)bB, (float)aB);
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        if (outline) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(rO, gO, bO, aO).endVertex();
            tessellator.draw();
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawTexts(AxisAlignedBB pos, String text) {
        if (pos == null || text == null) {
            return;
        }
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float)pos.minX + 0.5f, (float)pos.minY + 0.5f, (float)pos.minZ + 0.5f, RenderUtil.mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate((double)(-((double)OctoHack.textManager.getStringWidth(text) / 2.0)), (double)0.0, (double)0.0);
        OctoHack.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBlockESP(BlockPos pos, Color color, float linewidth) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        RenderUtil.drawBoundingBox(iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z), linewidth, ColorUtil.toRGBA(color));
    }

    public static void blockEsp(BlockPos blockPos, Color c, double length, double length2) {
        double x = (double)blockPos.getX() - RenderUtil.mc.renderManager.renderPosX;
        double y = (double)blockPos.getY() - RenderUtil.mc.renderManager.renderPosY;
        double z = (double)blockPos.getZ() - RenderUtil.mc.renderManager.renderPosZ;
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d((float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getBlue() / 255.0f, 0.25);
        RenderUtil.drawColorBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length), 0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glColor4d(0.0, 0.0, 0.0, 0.5);
        RenderUtil.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length));
        GL11.glLineWidth(2.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void glrendermethod() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        double viewerPosX = RenderUtil.mc.getRenderManager().viewerPosX;
        double viewerPosY = RenderUtil.mc.getRenderManager().viewerPosY;
        double viewerPosZ = RenderUtil.mc.getRenderManager().viewerPosZ;
        GL11.glPushMatrix();
        GL11.glTranslated(-viewerPosX, -viewerPosY, -viewerPosZ);
    }

    public static void glStart(float n, float n2, float n3, float n4) {
        RenderUtil.glrendermethod();
        GL11.glColor4f(n, n2, n3, n4);
    }

    public static void glEnd() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static AxisAlignedBB getBoundingBox(BlockPos blockPos) {
        return RenderUtil.mc.world.getBlockState(blockPos).getSelectedBoundingBox(RenderUtil.mc.world, blockPos).offset(blockPos);
    }

    public static void drawOutlinedBox(AxisAlignedBB axisAlignedBB) {
        GL11.glBegin(1);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();
    }

    public static void drawFilledBoxESPN(BlockPos pos, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        int rgba = ColorUtil.toRGBA(color);
        RenderUtil.drawFilledBox(bb, rgba);
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double)((double)x - RenderUtil.mc.getRenderManager().renderPosX), (double)((double)y - RenderUtil.mc.getRenderManager().renderPosY), (double)((double)z - RenderUtil.mc.getRenderManager().renderPosZ));
        GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-RenderUtil.mc.player.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)RenderUtil.mc.player.rotationPitch, (float)(RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((float)(-scale), (float)(-scale), (float)scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        RenderUtil.glBillboard(x, y, z);
        int distance = (int)player.getDistance(x, y, z);
        float scaleDistance = (float)distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale((float)scaleDistance, (float)scaleDistance, (float)scaleDistance);
    }

    public static void drawColoredBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 0.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, 0.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, 0.0f).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, 0.0f).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 0.0f).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
        Sphere s = new Sphere();
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(1.2f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        s.setDrawStyle(100013);
        GL11.glTranslated(x - RenderUtil.mc.renderManager.renderPosX, y - RenderUtil.mc.renderManager.renderPosY, z - RenderUtil.mc.renderManager.renderPosZ);
        s.draw(size, slices, stacks);
        GL11.glLineWidth(2.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void GLPre(float lineWidth) {
        depth = GL11.glIsEnabled(2896);
        texture = GL11.glIsEnabled(3042);
        clean = GL11.glIsEnabled(3553);
        bind = GL11.glIsEnabled(2929);
        override = GL11.glIsEnabled(2848);
        RenderUtil.GLPre(depth, texture, clean, bind, override, lineWidth);
    }

    public static void GlPost() {
        RenderUtil.GLPost(depth, texture, clean, bind, override);
    }

    private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
        if (depth) {
            GL11.glDisable(2896);
        }
        if (!texture) {
            GL11.glEnable(3042);
        }
        GL11.glLineWidth(lineWidth);
        if (clean) {
            GL11.glDisable(3553);
        }
        if (bind) {
            GL11.glDisable(2929);
        }
        if (!override) {
            GL11.glEnable(2848);
        }
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(3154, 4354);
        GlStateManager.depthMask((boolean)false);
    }

    public static float[][] getBipedRotations(ModelBiped biped) {
        float[][] rotations = new float[5][];
        float[] headRotation = new float[]{biped.bipedHead.rotateAngleX, biped.bipedHead.rotateAngleY, biped.bipedHead.rotateAngleZ};
        rotations[0] = headRotation;
        float[] rightArmRotation = new float[]{biped.bipedRightArm.rotateAngleX, biped.bipedRightArm.rotateAngleY, biped.bipedRightArm.rotateAngleZ};
        rotations[1] = rightArmRotation;
        float[] leftArmRotation = new float[]{biped.bipedLeftArm.rotateAngleX, biped.bipedLeftArm.rotateAngleY, biped.bipedLeftArm.rotateAngleZ};
        rotations[2] = leftArmRotation;
        float[] rightLegRotation = new float[]{biped.bipedRightLeg.rotateAngleX, biped.bipedRightLeg.rotateAngleY, biped.bipedRightLeg.rotateAngleZ};
        rotations[3] = rightLegRotation;
        float[] leftLegRotation = new float[]{biped.bipedLeftLeg.rotateAngleX, biped.bipedLeftLeg.rotateAngleY, biped.bipedLeftLeg.rotateAngleZ};
        rotations[4] = leftLegRotation;
        return rotations;
    }

    private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
        GlStateManager.depthMask((boolean)true);
        if (!override) {
            GL11.glDisable(2848);
        }
        if (bind) {
            GL11.glEnable(2929);
        }
        if (clean) {
            GL11.glEnable(3553);
        }
        if (!texture) {
            GL11.glDisable(3042);
        }
        if (depth) {
            GL11.glEnable(2896);
        }
    }

    public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
        GL11.glBegin(4);
        int i = (int)((float)num_segments / (360.0f / start_angle)) + 1;
        while ((float)i <= (float)num_segments / (360.0f / end_angle)) {
            double previousangle = Math.PI * 2 * (double)(i - 1) / (double)num_segments;
            double angle = Math.PI * 2 * (double)i / (double)num_segments;
            GL11.glVertex2d(cx, cy);
            GL11.glVertex2d((double)cx + Math.cos(angle) * (double)r, (double)cy + Math.sin(angle) * (double)r);
            GL11.glVertex2d((double)cx + Math.cos(previousangle) * (double)r, (double)cy + Math.sin(previousangle) * (double)r);
            ++i;
        }
        RenderUtil.glEnd();
    }

    public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
        GL11.glBegin(2);
        int i = (int)((float)num_segments / (360.0f / start_angle)) + 1;
        while ((float)i <= (float)num_segments / (360.0f / end_angle)) {
            double angle = Math.PI * 2 * (double)i / (double)num_segments;
            GL11.glVertex2d((double)cx + Math.cos(angle) * (double)r, (double)cy + Math.sin(angle) * (double)r);
            ++i;
        }
        RenderUtil.glEnd();
    }

    public static Vec3d updateToCamera(Vec3d vec) {
        return new Vec3d(vec.x - RenderUtil.mc.getRenderManager().viewerPosX, vec.y - RenderUtil.mc.getRenderManager().viewerPosY, vec.z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void drawCircleOutline(float x, float y, float radius) {
        RenderUtil.drawCircleOutline(x, y, radius, 0, 360, 40);
    }

    public static void drawCircleOutline(float x, float y, float radius, int start, int end, int segments) {
        RenderUtil.drawArcOutline(x, y, radius, start, end, segments);
    }

    public static void drawCircle(float x, float y, float radius) {
        RenderUtil.drawCircle(x, y, radius, 0, 360, 64);
    }

    public static void drawCircle(float x, float y, float radius, int start, int end, int segments) {
        RenderUtil.drawArc(x, y, radius, start, end, segments);
    }

    public static void drawOutlinedRoundedRectangle(int x, int y, int width, int height, float radius, float dR, float dG, float dB, float dA, float outlineWidth) {
        RenderUtil.drawRoundedRectangle(x, y, width, height, radius);
        GL11.glColor4f(dR, dG, dB, dA);
        RenderUtil.drawRoundedRectangle((float)x + outlineWidth, (float)y + outlineWidth, (float)width - outlineWidth * 2.0f, (float)height - outlineWidth * 2.0f, radius);
    }

    public static void drawRectangle(float x, float y, float width, float height) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(2);
        GL11.glVertex2d(width, 0.0);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, height);
        GL11.glVertex2d(width, height);
        RenderUtil.glEnd();
    }

    public static void drawCircle(float x, float y, float z, float radius, Color color) {
        BlockPos pos = new BlockPos(x, y, z);
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            RenderUtil.drawCircleVertices(bb, radius, color);
        }
    }

    public static void drawCircleVertices(AxisAlignedBB bb, float radius, Color color) {
        float r = (float)color.getRed() / 255.0f;
        float g = (float)color.getGreen() / 255.0f;
        float b = (float)color.getBlue() / 255.0f;
        float a = (float)color.getAlpha() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
        for (int i = 0; i < 360; ++i) {
            buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(bb.getCenter().x + Math.sin((double)i * 3.1415926 / 180.0) * (double)radius, bb.minY, bb.getCenter().z + Math.cos((double)i * 3.1415926 / 180.0) * (double)radius).color(r, g, b, a).endVertex();
            buffer.pos(bb.getCenter().x + Math.sin((double)(i + 1) * 3.1415926 / 180.0) * (double)radius, bb.minY, bb.getCenter().z + Math.cos((double)(i + 1) * 3.1415926 / 180.0) * (double)radius).color(r, g, b, a).endVertex();
            tessellator.draw();
        }
        GL11.glDisable(2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRectangleXY(float x, float y, float width, float height) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(2);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        RenderUtil.glEnd();
    }

    public static void drawFilledRectangle(float x, float y, float width, float height) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        RenderUtil.glEnd();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius, (double)y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius);
        }
        GL11.glEnd();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRectangle(float x, float y, float width, float height, float radius) {
        GL11.glEnable(3042);
        RenderUtil.drawArc(x + width - radius, y + height - radius, radius, 0.0f, 90.0f, 16);
        RenderUtil.drawArc(x + radius, y + height - radius, radius, 90.0f, 180.0f, 16);
        RenderUtil.drawArc(x + radius, y + radius, radius, 180.0f, 270.0f, 16);
        RenderUtil.drawArc(x + width - radius, y + radius, radius, 270.0f, 360.0f, 16);
        GL11.glBegin(4);
        GL11.glVertex2d(x + width - radius, y);
        GL11.glVertex2d(x + radius, y);
        GL11.glVertex2d(x + width - radius, y + radius);
        GL11.glVertex2d(x + width - radius, y + radius);
        GL11.glVertex2d(x + radius, y);
        GL11.glVertex2d(x + radius, y + radius);
        GL11.glVertex2d(x + width, y + radius);
        GL11.glVertex2d(x, y + radius);
        GL11.glVertex2d(x, y + height - radius);
        GL11.glVertex2d(x + width, y + radius);
        GL11.glVertex2d(x, y + height - radius);
        GL11.glVertex2d(x + width, y + height - radius);
        GL11.glVertex2d(x + width - radius, y + height - radius);
        GL11.glVertex2d(x + radius, y + height - radius);
        GL11.glVertex2d(x + width - radius, y + height);
        GL11.glVertex2d(x + width - radius, y + height);
        GL11.glVertex2d(x + radius, y + height - radius);
        GL11.glVertex2d(x + radius, y + height);
        RenderUtil.glEnd();
        GL11.glDisable(3042);
    }

    public static void renderOne(float lineWidth) {
        RenderUtil.checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderFour(Color color) {
        RenderUtil.setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public static void setColor(Color color) {
        GL11.glColor4d((double)color.getRed() / 255.0, (double)color.getGreen() / 255.0, (double)color.getBlue() / 255.0, (double)color.getAlpha() / 255.0);
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = RenderUtil.mc.framebufferMc;
        if (fbo != null && fbo.depthBuffer > -1) {
            RenderUtil.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, RenderUtil.mc.displayWidth, RenderUtil.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
    }

    public static void drawTextShadow(String msg, int x, int y, int color, double scale2) {
        RenderUtil.mc.fontRenderer.drawStringWithShadow(msg, x, y, color);
    }

    public static int getTextWidth(String text, double scale) {
        return (int)((double)RenderUtil.mc.fontRenderer.getStringWidth(text) * scale);
    }

    public static int getTextHeight(double scale) {
        return (int)((double)RenderUtil.mc.fontRenderer.FONT_HEIGHT * scale);
    }

    static {
        BufferBuilder = tessellator.getBuffer();
        itemRender = mc.getRenderItem();
        camera = new Frustum();
        depth = GL11.glIsEnabled(2896);
        texture = GL11.glIsEnabled(3042);
        clean = GL11.glIsEnabled(3553);
        bind = GL11.glIsEnabled(2929);
        override = GL11.glIsEnabled(2848);
        builder = tessellator.getBuffer();
    }

    public static class RenderTesselator
    extends Tessellator {
        public static RenderTesselator INSTANCE = new RenderTesselator();

        public RenderTesselator() {
            super(0x200000);
        }

        public static void prepare(int mode) {
            RenderTesselator.prepareGL();
            RenderTesselator.begin(mode);
        }

        public static void prepareGL() {
            GL11.glBlendFunc(770, 771);
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth((float)1.5f);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
        }

        public static void begin(int mode) {
            INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
        }

        public static void release() {
            RenderTesselator.render();
            RenderTesselator.releaseGL();
        }

        public static void render() {
            INSTANCE.draw();
        }

        public static void releaseGL() {
            GlStateManager.enableCull();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }

        public static void drawBox(BlockPos blockPos, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawBox(blockPos, r, g, b, a, sides);
        }

        public static void drawBox(float x, float y, float z, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawBox(INSTANCE.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
        }

        public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
            RenderTesselator.drawBox(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
        }

        public static BufferBuilder getBufferBuilder() {
            return INSTANCE.getBuffer();
        }

        public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
            if ((sides & 1) != 0) {
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 2) != 0) {
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 4) != 0) {
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 8) != 0) {
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x10) != 0) {
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x20) != 0) {
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            }
        }

        public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
            if ((sides & 0x11) != 0) {
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x12) != 0) {
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x21) != 0) {
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x22) != 0) {
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 5) != 0) {
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 6) != 0) {
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 9) != 0) {
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0xA) != 0) {
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x14) != 0) {
                buffer.pos(x, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x24) != 0) {
                buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x18) != 0) {
                buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            }
            if ((sides & 0x28) != 0) {
                buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
                buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            }
        }

        public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(width);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            tessellator.draw();
            GL11.glDisable(2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawFullBox(bb, blockPos, width, r, g, b, a, alpha2);
        }

        public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
            RenderTesselator.prepare(7);
            RenderTesselator.drawBox(blockPos, red, green, blue, alpha, 63);
            RenderTesselator.release();
            RenderTesselator.drawBoundingBox(bb, width, red, green, blue, alpha2);
        }

        public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawHalfBox(blockPos, r, g, b, a, sides);
        }

        public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
            RenderTesselator.drawBox(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 0.5f, 1.0f, r, g, b, a, sides);
        }
    }

    public static final class GeometryMasks {
        public static final HashMap FACEMAP = new HashMap();

        static {
            FACEMAP.put(EnumFacing.DOWN, 1);
            FACEMAP.put(EnumFacing.WEST, 16);
            FACEMAP.put(EnumFacing.NORTH, 4);
            FACEMAP.put(EnumFacing.SOUTH, 8);
            FACEMAP.put(EnumFacing.EAST, 32);
            FACEMAP.put(EnumFacing.UP, 2);
        }

        public static final class Line {
            public static final int DOWN_WEST = 17;
            public static final int UP_WEST = 18;
            public static final int DOWN_EAST = 33;
            public static final int UP_EAST = 34;
            public static final int DOWN_NORTH = 5;
            public static final int UP_NORTH = 6;
            public static final int DOWN_SOUTH = 9;
            public static final int UP_SOUTH = 10;
            public static final int NORTH_WEST = 20;
            public static final int NORTH_EAST = 36;
            public static final int SOUTH_WEST = 24;
            public static final int SOUTH_EAST = 40;
            public static final int ALL = 63;
        }

        public static final class Quad {
            public static final int DOWN = 1;
            public static final int UP = 2;
            public static final int NORTH = 4;
            public static final int SOUTH = 8;
            public static final int WEST = 16;
            public static final int EAST = 32;
            public static final int ALL = 63;
        }
    }
}
