package me.primooctopus33.octohack.mixin.mixins;

import java.io.IOException;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.gui.components.DupeButton;
import me.primooctopus33.octohack.client.modules.client.IllegalStackDupeButton;
import me.primooctopus33.octohack.client.modules.exploit.ManualIllegalStack;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiContainer.class})
public abstract class MixinGuiContainer
extends GuiScreen {
    private DupeButton dupeButton;
    private final IllegalStackDupeButton dupeModule = OctoHack.moduleManager.getModuleByClass(IllegalStackDupeButton.class);
    private final ManualIllegalStack manualDupeModule = OctoHack.moduleManager.getModuleByClass(ManualIllegalStack.class);
    @Shadow
    protected int field_147003_i;
    @Shadow
    protected int field_147009_r;

    @Inject(method={"initGui"}, at={@At(value="RETURN")})
    public void initGui(CallbackInfo info) {
        this.buttonList.clear();
        this.dupeButton = new DupeButton(1338, this.width / 2 - 50, this.field_147009_r - 20, "Dupe");
        this.buttonList.add(this.dupeButton);
        this.dupeButton.setWidth(100);
        this.updateButton();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1338) {
            this.manualDupeModule.toggle();
        } else {
            super.actionPerformed(button);
        }
    }

    @Inject(method={"updateScreen"}, at={@At(value="HEAD")})
    public void updateScreen(CallbackInfo ci) {
        this.updateButton();
    }

    private void updateButton() {
        if (this.dupeModule.isEnabled() && this.dupeModule.validGui) {
            this.dupeButton.visible = true;
            this.dupeButton.displayString = "Dupe";
        } else {
            this.dupeButton.visible = false;
        }
    }
}
