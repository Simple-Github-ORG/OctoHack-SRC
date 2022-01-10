package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvent
extends EventStage {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}
