package me.primooctopus33.octohack.client.gui;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.gui.components.Component;
import me.primooctopus33.octohack.client.gui.components.items.buttons.ModuleButton;
import me.primooctopus33.octohack.client.modules.Module;

class OctoHackGui$1
extends Component {
    final Module.Category val$category;

    OctoHackGui$1(String name, int x, int y, boolean open, Module.Category category) {
        this.val$category = category;
        super(name, x, y, open);
    }

    @Override
    public void setupItems() {
        counter1 = new int[]{1};
        OctoHack.moduleManager.getModulesByCategory(this.val$category).forEach(module -> {
            if (!module.hidden) {
                this.addButton(new ModuleButton((Module)module));
            }
        });
    }
}
