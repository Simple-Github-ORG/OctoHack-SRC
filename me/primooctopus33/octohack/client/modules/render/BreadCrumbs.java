package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class BreadCrumbs
extends Module {
    public static Setting<Integer> length;
    public static Setting<Float> width;
    public static Setting<Boolean> syncColor;
    public static Setting<Integer> red;
    public static Setting<Integer> green;
    public static Setting<Integer> blue;
    public static Setting<Integer> alpha;
    public static ArrayList<double[]> vecs;
    public Color color;

    public BreadCrumbs() {
        super("BreadCrumbs", "Draws a small line behind you", Module.Category.RENDER, true, false, false);
        length = this.register(new Setting<Integer>("Length", 15, 5, 40));
        width = this.register(new Setting<Float>("Width", Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(15.599429f) ^ 0x7EB99743)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.076195f) ^ 0x7F04E061)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.3190416f) ^ 0x7F08D65B))));
        syncColor = this.register(new Setting<Boolean>("Sync", false));
        red = this.register(new Setting<Integer>("Red", 30, 0, 255));
        green = this.register(new Setting<Integer>("Green", 167, 0, 255));
        blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
        alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
        vecs = new ArrayList();
    }

    public Color getCurrentColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
    }

    @Override
    public void onUpdate() {
        this.color = syncColor.getValue() != false ? ClickGui.getInstance().getCurrentColor() : this.getCurrentColor();
        try {
            double renderPosX = BreadCrumbs.mc.getRenderManager().renderPosX;
            double renderPosY = BreadCrumbs.mc.getRenderManager().renderPosY;
            double renderPosZ = BreadCrumbs.mc.getRenderManager().renderPosZ;
            if (this.isEnabled()) {
                for (EntityPlayer next : BreadCrumbs.mc.world.playerEntities) {
                    if (!(next instanceof EntityPlayer)) continue;
                    EntityPlayer entityPlayer = next;
                    boolean b = entityPlayer == BreadCrumbs.mc.player;
                    double n = renderPosY + Double.longBitsToDouble(Double.doubleToLongBits(0.48965838138858014) ^ 0x7FDF56901B91AE07L);
                    if (BreadCrumbs.mc.player.isElytraFlying()) {
                        n -= Double.longBitsToDouble(Double.doubleToLongBits(29.56900080933637) ^ 0x7FC591AA097B7F4BL);
                    }
                    if (!b) continue;
                    vecs.add(new double[]{renderPosX, n - (double)entityPlayer.height, renderPosZ});
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (vecs.size() > length.getValue()) {
            vecs.remove(0);
        }
    }

    @Override
    public void onDisable() {
        vecs.removeAll(vecs);
    }

    public static double M(double n) {
        if (n == Double.longBitsToDouble(Double.doubleToLongBits(1.7931000183463725E308) ^ 0x7FEFEB11C3AAD037L)) {
            return n;
        }
        if (n < Double.longBitsToDouble(Double.doubleToLongBits(1.1859585260803721E308) ^ 0x7FE51C5AEE8AD07FL)) {
            return n * Double.longBitsToDouble(Double.doubleToLongBits(-12.527781766526259) ^ 0x7FD90E3969654F8FL);
        }
        return n;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        try {
            double renderPosX = BreadCrumbs.mc.getRenderManager().renderPosX;
            double renderPosY = BreadCrumbs.mc.getRenderManager().renderPosY;
            double renderPosZ = BreadCrumbs.mc.getRenderManager().renderPosZ;
            float n = (float)this.color.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.49987957f) ^ 0x7D80F037);
            float n2 = (float)this.color.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.4340212f) ^ 0x7DA13807);
            float n3 = (float)this.color.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.0131841665f) ^ 0x7F270267);
            if (this.isEnabled()) {
                Iterator<double[]> iterator3;
                RenderUtil.prepareGL();
                GL11.glPushMatrix();
                GL11.glEnable(2848);
                GL11.glLineWidth(width.getValue().floatValue());
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth(width.getValue().floatValue());
                GL11.glDepthMask(false);
                GL11.glBegin(3);
                Iterator<double[]> iterator2 = iterator3 = vecs.iterator();
                while (iterator3.hasNext()) {
                    double d;
                    double[] array = iterator2.next();
                    double m = BreadCrumbs.M(Math.hypot(array[0] - BreadCrumbs.mc.player.posX, array[1] - BreadCrumbs.mc.player.posY));
                    if (d > (double)length.getValue().intValue()) {
                        iterator3 = iterator2;
                        continue;
                    }
                    GL11.glColor4f(n, n2, n3, Float.intBitsToFloat(Float.floatToIntBits(14.099797f) ^ 0x7EE198C5) - (float)(m / (double)length.getValue().intValue()));
                    iterator3 = iterator2;
                    GL11.glVertex3d(array[0] - renderPosX, array[1] - renderPosY, array[2] - renderPosZ);
                }
                GL11.glEnd();
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
                RenderUtil.releaseGL();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
