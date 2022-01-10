package me.primooctopus33.octohack.event;

import me.primooctopus33.octohack.event.EnumStages;
import me.primooctopus33.octohack.event.events.ICancellable;

public class OctoHackEvent
implements ICancellable {
    private final EnumStages stage;
    private boolean canceled;

    public OctoHackEvent(EnumStages stage) {
        this.stage = stage;
    }

    public EnumStages getStage() {
        return this.stage;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

    public void resume() {
        this.canceled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }
}
