package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PerspectiveEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AspectRatio
extends Module {
    public Setting<Double> aspect;

    public AspectRatio() {
        super("AspectRatio", "Allows you to change your aspect ratio", Module.Category.RENDER, true, false, false);
        this.aspect = this.register(new Setting<Double>("aspect", (double)(AspectRatio.mc.displayWidth / AspectRatio.mc.displayHeight) + 0.0, 0.0, 3.0));
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}
