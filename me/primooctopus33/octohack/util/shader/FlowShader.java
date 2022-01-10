package me.primooctopus33.octohack.util.shader;

import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class FlowShader
extends FramebufferShader {
    public static FlowShader FLOW_SHADER = new FlowShader();
    public float time;

    public FlowShader() {
        super("flow.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), Float.intBitsToFloat(Float.floatToIntBits(12.494699f) ^ 0x7EC7EA49));
        this.time += Float.intBitsToFloat(Float.floatToIntBits(24055.986f) ^ 0x7DFF745F) * (float)RenderUtil.deltaTime;
    }
}
