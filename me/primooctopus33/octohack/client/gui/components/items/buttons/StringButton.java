package me.primooctopus33.octohack.client.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.gui.OctoHackGui;
import me.primooctopus33.octohack.client.gui.components.items.buttons.Button;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.SoundEvent;

public class StringButton
extends Button {
    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? OctoHack.colorManager.getColorWithAlpha(OctoHack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : OctoHack.colorManager.getColorWithAlpha(OctoHack.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        if (this.isListening) {
            OctoHack.textManager.drawStringWithShadow(this.currentString.getString() + OctoHack.textManager.getIdleSign(), this.x + 2.3f, this.y - 4.0f - (float)OctoHackGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        } else {
            OctoHack.textManager.drawStringWithShadow((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.RED : "")) + this.setting.getValue(), this.x + 2.3f, this.y - 4.0f - (float)OctoHackGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    this.enterString();
                }
                case 14: {
                    this.setString(StringButton.removeLastChar(this.currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter((char)typedChar)) {
                this.setString(this.currentString.getString() + typedChar);
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        this.onMouseClick();
    }

    @Override
    public int getHeight() {
        return 11;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
