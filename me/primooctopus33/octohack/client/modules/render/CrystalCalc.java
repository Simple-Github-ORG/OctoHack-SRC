package me.primooctopus33.octohack.client.modules.render;

import java.util.ArrayList;
import java.util.List;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrystalCalc
extends Module {
    private final ResourceLocation crystalimage = new ResourceLocation("textures/crystalimage.png");
    private final List<BlockPos> pos = new ArrayList<BlockPos>();
    private BlockPos renderPos;

    public CrystalCalc() {
        super("CrystalCalc", "draws various calculations under end crystals", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        this.pos.clear();
        for (Entity entity : CrystalCalc.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            BlockPos blockPos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY - 0.5), Math.floor(entity.posZ));
            this.pos.add(blockPos);
        }
    }

    public static void drawCompleteImage(float posX, float posY, float posZ, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, posZ);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (BlockPos blockPos : this.pos) {
            GL11.glPushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.translate((double)((double)blockPos.getX() + 0.5), (double)(blockPos.getY() - 1), (double)((double)blockPos.getZ() + 0.5));
            mc.getTextureManager().bindTexture(this.crystalimage);
            Gui.drawScaledCustomSizeModalRect((int)10, (int)-17, (float)0.0f, (float)0.0f, (int)12, (int)12, (int)22, (int)22, (float)12.0f, (float)12.0f);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
            GL11.glPopMatrix();
        }
    }
}
