package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.BlockPushEvent;
import me.primooctopus33.octohack.event.events.PushEvent;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoPush
extends Module {
    private float savedReduction;

    public NoPush() {
        super("NoPush", "Stops entites and blocks from pushing you", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        NoPush.mc.player.entityCollisionReduction = 1.0f;
    }

    @Override
    public void onEnable() {
        this.savedReduction = NoPush.mc.player != null ? NoPush.mc.player.entityCollisionReduction : 0.0f;
    }

    @Override
    public void onDisable() {
        NoPush.mc.player.entityCollisionReduction = this.savedReduction;
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockPush(BlockPushEvent event) {
        event.setCanceled(true);
    }
}
