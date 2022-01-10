package me.primooctopus33.octohack.client.modules.render;

import me.primooctopus33.octohack.client.modules.Module;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockView
extends Module {
    public BlockView() {
        super("BlockView", "See clearly when inside blocks", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onRenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) {
            event.setCanceled(true);
        }
    }
}
