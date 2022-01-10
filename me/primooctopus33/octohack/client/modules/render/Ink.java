package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ink
extends Module {
    public final Setting<Boolean> increase = this.register(new Setting<Boolean>("Increase", false));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 100, 0, 250));
    public Timer timer = new Timer();
    public int increaseAmount = 1;

    public Ink() {
        super("Ink", "Splatters ink all over your screen", Module.Category.RENDER, true, false, false);
    }

    @Override
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (this.increase.getValue().booleanValue()) {
            if (this.timer.passedMs(this.delay.getValue().intValue())) {
                ++this.increaseAmount;
                this.timer.reset();
            }
        } else {
            this.timer.reset();
        }
        int color = ColorUtil.toARGB(0, 0, 0, 200);
        ScaledResolution resolution = new ScaledResolution(mc);
        float midX = (float)resolution.getScaledWidth() / 2.0f;
        float midY = (float)resolution.getScaledHeight() / 2.0f;
        RenderUtil.drawCircle(midX, midY, this.increase.getValue() != false ? (float)this.increaseAmount : 30.0f, color);
        RenderUtil.drawCircle(midX + 50.0f, midY + 30.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 50.0f, color);
        RenderUtil.drawCircle(midX - 150.0f, midY, this.increase.getValue() != false ? (float)this.increaseAmount : 50.0f, color);
        RenderUtil.drawCircle(midX + 150.0f, midY - 70.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 70.0f, color);
        RenderUtil.drawCircle(midX - 200.0f, midY + 100.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 100.0f, color);
        RenderUtil.drawCircle(midX - 200.0f, midY - 100.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 50.0f, color);
        RenderUtil.drawCircle(midX + 250.0f, midY - 100.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 50.0f, color);
        RenderUtil.drawCircle(midX + 250.0f, midY + 150.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 50.0f, color);
        RenderUtil.drawCircle(midX - 30.0f, midY - 100.0f, this.increase.getValue() != false ? (float)this.increaseAmount : 150.0f, color);
    }

    @Override
    public void onDisable() {
        this.increaseAmount = 0;
    }
}
