package me.primooctopus33.octohack.event.events;

import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent {
    private GuiScreen screen;

    public GuiScreenEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return this.screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }

    public static class Closed
    extends GuiScreenEvent {
        public Closed(GuiScreen screen) {
            super(screen);
        }
    }

    public static class Displayed
    extends GuiScreenEvent {
        public Displayed(GuiScreen screen) {
            super(screen);
        }
    }
}
