package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomCrosshair
extends Module {
    private final Setting<Boolean> dynamic = this.register(new Setting<Boolean>("Dynamic", true));
    private final Setting<Float> width = this.register(new Setting<Float>("Width", Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(10.0f)));
    private final Setting<Float> gap = this.register(new Setting<Float>("Gap", Float.valueOf(3.0f), Float.valueOf(0.5f), Float.valueOf(10.0f)));
    private final Setting<Float> length = this.register(new Setting<Float>("Length", Float.valueOf(7.0f), Float.valueOf(0.5f), Float.valueOf(100.0f)));
    private final Setting<Float> dynamicGap = this.register(new Setting<Float>("DynamicGap", Float.valueOf(1.5f), Float.valueOf(0.5f), Float.valueOf(10.0f)));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private final Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    private final Setting<Boolean> staticRainbow = this.register(new Setting<Boolean>("Static Rainbow", false));

    public CustomCrosshair() {
        super("CustomCrosshair", "Lets you customize your in game crosshair", Module.Category.RENDER, true, false, false);
    }

    @Override
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        Color rai = new Color(ColorUtil.getRainbow(6000, -15.0f));
        int color = this.staticRainbow.getValue() != false ? this.color(2, 100) : (this.rainbow.getValue() != false ? new Color(rai.getRed(), rai.getGreen(), rai.getBlue(), this.alpha.getValue()).getRGB() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()).getRGB());
        ScaledResolution resolution = new ScaledResolution(mc);
        float middlex = (float)resolution.getScaledWidth() / 2.0f;
        float middley = (float)resolution.getScaledHeight() / 2.0f;
        RenderUtil.drawBordered(middlex - this.width.getValue().floatValue(), middley - (this.gap.getValue().floatValue() + this.length.getValue().floatValue()) - (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middlex + this.width.getValue().floatValue(), middley - this.gap.getValue().floatValue() - (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), 0.5f, color, -16777216);
        RenderUtil.drawBordered(middlex - this.width.getValue().floatValue(), middley + this.gap.getValue().floatValue() + (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middlex + this.width.getValue().floatValue(), middley + (this.gap.getValue().floatValue() + this.length.getValue().floatValue()) + (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), 0.5f, color, -16777216);
        RenderUtil.drawBordered(middlex - (this.gap.getValue().floatValue() + this.length.getValue().floatValue()) - (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middley - this.width.getValue().floatValue(), middlex - this.gap.getValue().floatValue() - (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middley + this.width.getValue().floatValue(), 0.5f, color, -16777216);
        RenderUtil.drawBordered(middlex + this.gap.getValue().floatValue() + (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middley - this.width.getValue().floatValue(), middlex + (this.gap.getValue().floatValue() + this.length.getValue().floatValue()) + (this.isMoving() && this.dynamic.getValue() != false ? this.dynamicGap.getValue().floatValue() : 0.0f), middley + this.width.getValue().floatValue(), 0.5f, color, -16777216);
    }

    public boolean isMoving() {
        return CustomCrosshair.mc.player.moveForward != 0.0f || CustomCrosshair.mc.player.moveStrafing != 0.0f || CustomCrosshair.mc.player.moveVertical != 0.0f;
    }

    public int color(int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), hsb);
        float brightness = Math.abs((CustomCrosshair.getOffset() + (float)index / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.4f + 0.4f * brightness;
        hsb[2] = brightness % 1.0f;
        Color clr = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), this.alpha.getValue()).getRGB();
    }

    private static float getOffset() {
        return (float)(System.currentTimeMillis() % 2000L) / 1000.0f;
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (CustomCrosshair.mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);
        }
    }
}
