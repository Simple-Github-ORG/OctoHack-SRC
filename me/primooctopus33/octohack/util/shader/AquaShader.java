package me.primooctopus33.octohack.util.shader;

import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class AquaShader
extends FramebufferShader {
    public static AquaShader AQUA_SHADER = new AquaShader();
    public float time;

    public AquaShader() {
        super("aqua.frag");
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
        this.time += Float.intBitsToFloat(Float.floatToIntBits(1015.0615f) ^ 0x7F395856) * (float)RenderUtil.deltaTime;
    }
}
