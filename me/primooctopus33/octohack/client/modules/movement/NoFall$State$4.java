package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.movement.NoFall;

final class NoFall$State$4
extends NoFall.State {
    @Override
    public NoFall.State onUpdate() {
        if (bypassTimer.passedMs(250L)) {
            return REEQUIP_ELYTRA;
        }
        return this;
    }
}
