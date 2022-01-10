package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.event.events.RenderEntityLayerEvent;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.shader.AquaShader;
import me.primooctopus33.octohack.util.shader.FlowShader;
import me.primooctopus33.octohack.util.shader.FramebufferShader;
import me.primooctopus33.octohack.util.shader.GalaxyShader;
import me.primooctopus33.octohack.util.shader.GlowShader;
import me.primooctopus33.octohack.util.shader.RainbowShader;
import me.primooctopus33.octohack.util.shader.RedShader;
import me.primooctopus33.octohack.util.shader.SmokeShader;
import me.primooctopus33.octohack.util.shader.StarShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShaderChams
extends Module {
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Aqua));
    public static boolean renderNameTags;

    public ShaderChams() {
        super("ShaderChams", "Draws chams over players with shaders", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        Float radius;
        if (ShaderChams.mc.player == null || ShaderChams.mc.world == null) {
            return;
        }
        FramebufferShader framebufferShader = null;
        switch (this.mode.getValue()) {
            case Smoke: {
                framebufferShader = SmokeShader.SMOKE_SHADER;
                break;
            }
            case Aqua: {
                framebufferShader = AquaShader.AQUA_SHADER;
                break;
            }
            case Flow: {
                framebufferShader = FlowShader.FLOW_SHADER;
                break;
            }
            case Red: {
                framebufferShader = RedShader.RED_SHADER;
                break;
            }
            case Star: {
                framebufferShader = StarShader.STAR_SHADER;
                break;
            }
            case Rainbow: {
                framebufferShader = RainbowShader.RAINBOW_SHADER;
                break;
            }
            case Galaxy: {
                framebufferShader = GalaxyShader.GALAXY_SHADER;
                break;
            }
            case Outline: {
                framebufferShader = GlowShader.GLOW_SHADER;
            }
        }
        SmokeShader framebufferShader2 = framebufferShader;
        if (framebufferShader2 == null) {
            return;
        }
        SmokeShader shader = framebufferShader2;
        GlStateManager.matrixMode((int)5889);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.matrixMode((int)5888);
        GlStateManager.pushMatrix();
        shader.startDraw(event.getPartialTicks());
        renderNameTags = false;
        try {
            for (Entity entity : ShaderChams.mc.world.loadedEntityList) {
                Render getEntityRenderObject;
                if (entity == ShaderChams.mc.player || entity == mc.getRenderViewEntity() || !(entity instanceof EntityPlayer) || (getEntityRenderObject = mc.getRenderManager().getEntityRenderObject(entity)) == null) continue;
                Render entityRenderObject = getEntityRenderObject;
                Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                entityRenderObject.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        renderNameTags = true;
        float n2 = Float.intBitsToFloat(Float.floatToIntBits(3.651715f) ^ 0x7F69B5B3);
        Float value3 = Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.9867451f) ^ 0x7F3C9B54));
        Float n3 = radius = Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1799.2811f) ^ 0x7BE0E8FF) + value3.floatValue());
        SmokeShader framebufferShader3 = shader;
        float red = Float.intBitsToFloat(Float.floatToIntBits(1.4528846f) ^ 0x7CC6F81F);
        float green = Float.intBitsToFloat(Float.floatToIntBits(8.874367E37f) ^ 0x7E8586D1);
        float blue = Float.intBitsToFloat(Float.floatToIntBits(0.01116983f) ^ 0x7F4801AA);
        float alpha = Float.intBitsToFloat(Float.floatToIntBits(0.008144599f) ^ 0x7F7A70ED);
        framebufferShader3.stopDraw(Float.intBitsToFloat(Float.floatToIntBits(0.010916991f) ^ 0x7F4DDD2E), Float.intBitsToFloat(Float.floatToIntBits(3.0171999E38f) ^ 0x7F62FD28), Float.intBitsToFloat(Float.floatToIntBits(0.00893931f) ^ 0x7F6D762F), Float.intBitsToFloat(Float.floatToIntBits(0.096559145f) ^ 0x7EBAC0CD), radius.floatValue(), Float.intBitsToFloat(Float.floatToIntBits(4.801641f) ^ 0x7F19A70B));
        GlStateManager.color((float)Float.intBitsToFloat(Float.floatToIntBits(4.0344067f) ^ 0x7F0119DC), (float)Float.intBitsToFloat(Float.floatToIntBits(10.789216f) ^ 0x7EACA0A1), (float)Float.intBitsToFloat(Float.floatToIntBits(5.1625485f) ^ 0x7F253399));
        GlStateManager.matrixMode((int)5889);
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.matrixMode((int)5888);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRenderEntityLayer(RenderEntityLayerEvent event) {
        if (!renderNameTags) {
            event.setCanceled(true);
        }
    }

    public static enum Mode {
        Smoke,
        Aqua,
        Flow,
        Red,
        Star,
        Rainbow,
        Galaxy,
        Outline;

    }
}
