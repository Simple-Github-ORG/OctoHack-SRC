package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.event.events.RenderEntityModelEvent;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton
extends Module {
    private static Skeleton INSTANCE = new Skeleton();
    private final Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Boolean> colorFriends = this.register(new Setting<Boolean>("Friends", true));
    private final Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Invisibles", false));
    private final Map<EntityPlayer, float[][]> rotationList = new HashMap<EntityPlayer, float[][]>();

    public Skeleton() {
        super("Skeleton", "Draws a skeleton on players", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static Skeleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Skeleton();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RenderUtil.GLPre(this.lineWidth.getValue().floatValue());
        for (EntityPlayer player : Skeleton.mc.world.playerEntities) {
            if (player == null || player == mc.getRenderViewEntity() || !player.isEntityAlive() || player.isPlayerSleeping() || player.isInvisible() && !this.invisibles.getValue().booleanValue() || this.rotationList.get(player) == null || !(Skeleton.mc.player.getDistanceSq(player) < 2500.0)) continue;
            this.renderSkeleton(player, this.rotationList.get(player), this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : EntityUtil.getColor(player, this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue() - 1, this.colorFriends.getValue()));
        }
        RenderUtil.GlPost();
    }

    public void onRenderModel(RenderEntityModelEvent event) {
        if (event.getStage() == 0 && event.entity instanceof EntityPlayer && event.modelBase instanceof ModelBiped) {
            ModelBiped biped = (ModelBiped)((Object)event.modelBase);
            float[][] rotations = RenderUtil.getBipedRotations(biped);
            EntityPlayer player = (EntityPlayer)event.entity;
            this.rotationList.put(player, rotations);
        }
    }

    private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.color((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        Vec3d interp = EntityUtil.getInterpolatedRenderPos(player, mc.getRenderPartialTicks());
        double pX = interp.x;
        double pY = interp.y;
        double pZ = interp.z;
        GlStateManager.translate((double)pX, (double)pY, (double)pZ);
        GlStateManager.rotate((float)(-player.renderYawOffset), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? -0.235 : 0.0));
        float sneak = player.isSneaking() ? 0.6f : 0.75f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)-0.125, (double)sneak, (double)0.0);
        if (rotations[3][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[3][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[3][2] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -sneak, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.125, (double)sneak, (double)0.0);
        if (rotations[4][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[4][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[4][2] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -sneak, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? 0.25 : 0.0));
        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (player.isSneaking()) {
            sneakOffset = -0.05;
        }
        GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.01725 : 0.0));
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)-0.375, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[1][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[1][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[1][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[1][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[1][2] != 0.0f) {
            GlStateManager.rotate((float)(-rotations[1][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -0.5, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.375, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[2][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[2][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[2][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[2][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[2][2] != 0.0f) {
            GlStateManager.rotate((float)(-rotations[2][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, -0.5, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[0][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[0][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.3, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.rotate((float)(player.isSneaking() ? 25.0f : 0.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        if (player.isSneaking()) {
            sneakOffset = -0.16175;
        }
        GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.48025 : 0.0));
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(-0.125, 0.0, 0.0);
        GL11.glVertex3d(0.125, 0.0, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.55, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
        GlStateManager.glBegin((int)3);
        GL11.glVertex3d(-0.375, 0.0, 0.0);
        GL11.glVertex3d(0.375, 0.0, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
}
