package me.primooctopus33.octohack.client.modules.misc;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class GhastTracer
extends Module {
    public Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Tracer Invisible Ghasts", true));
    public Setting<Integer> ghastRed = this.register(new Setting<Integer>("Ghast Red", 255, 0, 255));
    public Setting<Integer> ghastBlue = this.register(new Setting<Integer>("Ghast Blue", 255, 0, 255));
    public Setting<Integer> ghastGreen = this.register(new Setting<Integer>("Ghast Green", 255, 0, 255));
    public Setting<Integer> ghastAlpha = this.register(new Setting<Integer>("Ghast Alpha", 255, 0, 255));
    public Setting<Boolean> drawLineFromSky = this.register(new Setting<Boolean>("Tracer From Sky", false));
    public Setting<Integer> distance = this.register(new Setting<Integer>("Ghast Distance", 500, 1, 500));
    public Setting<Float> width = this.register(new Setting<Float>("Tracer Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));

    public GhastTracer() {
        super("GhastTracer", "Draws a tracer to ghasts", Module.Category.MISC, true, false, false);
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (GhastTracer.fullNullCheck()) {
            return;
        }
        GlStateManager.pushMatrix();
        GhastTracer.mc.world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> entity instanceof EntityGhast && GhastTracer.mc.player.getDistanceSq((Entity)entity) < MathUtil.square(this.distance.getValue().intValue())).filter(entity -> this.invisibles.getValue() != false || !entity.isInvisible()).forEach(entity -> this.drawLineToEntity((Entity)entity, this.ghastRed.getValue().intValue(), this.ghastGreen.getValue().intValue(), this.ghastBlue.getValue().intValue(), this.ghastAlpha.getValue().intValue()));
        GlStateManager.popMatrix();
    }

    public double interpolate(double now, double then) {
        return then + (now - then) * (double)mc.getRenderPartialTicks();
    }

    public double[] interpolate(Entity entity) {
        double posX = this.interpolate(entity.posX, entity.lastTickPosX) - GhastTracer.mc.getRenderManager().renderPosX;
        double posY = this.interpolate(entity.posY, entity.lastTickPosY) - GhastTracer.mc.getRenderManager().renderPosY;
        double posZ = this.interpolate(entity.posZ, entity.lastTickPosZ) - GhastTracer.mc.getRenderManager().renderPosZ;
        return new double[]{posX, posY, posZ};
    }

    public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
        double[] xyz = this.interpolate(e);
        this.drawLine(xyz[0], xyz[1], xyz[2], e.height, red, green, blue, opacity);
    }

    public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(GhastTracer.mc.player.rotationPitch))).rotateYaw(-((float)Math.toRadians(GhastTracer.mc.player.rotationYaw)));
        if (!this.drawLineFromSky.getValue().booleanValue()) {
            this.drawLineFromPosToPos(eyes.x, eyes.y + (double)GhastTracer.mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
        } else {
            this.drawLineFromPosToPos(posx, 256.0, posz, posx, posy, posz, up, red, green, blue, opacity);
        }
    }

    public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(this.width.getValue().floatValue());
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        GhastTracer.mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        GL11.glBegin(1);
        GL11.glVertex3d(posx, posy, posz);
        GL11.glVertex3d(posx2, posy2, posz2);
        GL11.glVertex3d(posx2, posy2, posz2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glColor3d(1.0, 1.0, 1.0);
        GlStateManager.enableLighting();
    }
}
