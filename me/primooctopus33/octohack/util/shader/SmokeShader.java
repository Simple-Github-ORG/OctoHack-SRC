package me.primooctopus33.octohack.util.shader;

import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class SmokeShader
extends FramebufferShader {
    public static SmokeShader SMOKE_SHADER = new SmokeShader();
    public float time;

    public SmokeShader() {
        super("smoke.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), this.time);
        this.time += Float.intBitsToFloat(Float.floatToIntBits(949.1068f) ^ 0x7F29DD70) * (float)RenderUtil.deltaTime;
    }
}
