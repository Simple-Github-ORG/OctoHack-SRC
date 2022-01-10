package me.primooctopus33.octohack.client.modules.client;

import java.awt.Color;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.modules.client.HUD;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketsCounter
extends Module {
    public final Setting<Integer> x = this.register(new Setting<Integer>("X", 2, 0, 1000));
    public final Setting<Integer> y = this.register(new Setting<Integer>("Y", 30, 0, 600));
    public Timer timer = new Timer();
    public int incoming = 0;
    public int outgoing = 0;

    public PacketsCounter() {
        super("PacketsCounter", "Counts the amount of packets sent and received per second", Module.Category.CLIENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedS(1.0)) {
            this.incoming = 0;
            this.outgoing = 0;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        ++this.incoming;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        ++this.outgoing;
    }

    @Override
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        Color color1 = new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        OctoHack.textManager.drawStringWithShadow("Packets Received/S : " + this.incoming, this.x.getValue().intValue(), this.y.getValue().intValue(), HUD.getInstance().flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(this.y.getValue() + 1) * 0.89f, color1) : ClickGui.getInstance().getCurrentColorHex());
        OctoHack.textManager.drawStringWithShadow("Packets Sent/S : " + this.outgoing, this.x.getValue().intValue(), this.y.getValue() - 15, HUD.getInstance().flowingArrayList.getValue() != false ? ColorUtil.staticRainbow((float)(this.y.getValue() + 1) * 0.89f, color1) : ClickGui.getInstance().getCurrentColorHex());
    }
}
