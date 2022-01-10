package me.primooctopus33.octohack.event.events;

import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientEvent
extends EventStage {
    private Feature feature;
    private Setting setting;

    public ClientEvent(int stage, Feature feature) {
        super(stage);
        this.feature = feature;
    }

    public ClientEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public Setting getSetting() {
        return this.setting;
    }
}
