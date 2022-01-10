package me.primooctopus33.octohack.client.gui.components.items.buttons;

import java.util.ArrayList;
import java.util.List;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.gui.OctoHackGui;
import me.primooctopus33.octohack.client.gui.components.Component;
import me.primooctopus33.octohack.client.gui.components.items.Item;
import me.primooctopus33.octohack.client.gui.components.items.buttons.BindButton;
import me.primooctopus33.octohack.client.gui.components.items.buttons.BooleanButton;
import me.primooctopus33.octohack.client.gui.components.items.buttons.Button;
import me.primooctopus33.octohack.client.gui.components.items.buttons.EnumButton;
import me.primooctopus33.octohack.client.gui.components.items.buttons.Slider;
import me.primooctopus33.octohack.client.gui.components.items.buttons.StringButton;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Bind;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

public class ModuleButton
extends Button {
    private final Module module;
    private final ResourceLocation crossimage = new ResourceLocation("textures/crossimage.png");
    private final ResourceLocation crossimagetwo = new ResourceLocation("textures/crossimagetwo.png");
    private List<Item> items = new ArrayList<Item>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<Item>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }
                if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton(setting));
                }
                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton(setting));
                }
                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new Slider(setting));
                    continue;
                }
                if (!setting.isEnumSetting()) continue;
                newItems.add(new EnumButton(setting));
            }
        }
        newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
        this.items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            if (ClickGui.getInstance().cross.getValue().booleanValue()) {
                mc.getTextureManager().bindTexture(this.crossimage);
                if (!this.subOpen) {
                    ModuleButton.drawCompleteImage(this.x - 4.0f + (float)this.width - 7.4f, this.y - 4.8f - (float)OctoHackGui.getClickGui().getTextOffset(), 10, 10);
                }
                mc.getTextureManager().bindTexture(this.crossimagetwo);
                if (this.subOpen) {
                    ModuleButton.drawCompleteImage(this.x - 4.0f + (float)this.width - 7.4f, this.y - 4.8f - (float)OctoHackGui.getClickGui().getTextOffset(), 10, 10);
                }
            }
            if (this.subOpen) {
                float height = 1.0f;
                for (Item item : this.items) {
                    Component.counter1[0] = Component.counter1[0] + 1;
                    if (!item.isHidden()) {
                        item.setLocation(this.x + 1.0f, this.y + (height += 12.0f));
                        item.setHeight(11);
                        item.setWidth(this.width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }
                    item.update();
                }
            }
        }
        if (!this.isHovering(mouseX, mouseY)) {
            return;
        }
        OctoHack.textManager.drawStringWithShadow(this.module.getDescription(), 22.0f, 14.0f - (float)OctoHackGui.getClickGui().getTextOffset(), -1);
        Gui.drawRect((int)19, (int)3, (int)(23 + OctoHack.textManager.getStringWidth(this.module.getDescription())), (int)16, (int)(ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ClickGui.getInstance().getCurrentColorHex()));
        OctoHack.textManager.drawStringWithShadow("DescriptionsHub", 22.0f, 0.0f - (float)OctoHackGui.getClickGui().getTextOffset(), -1);
        RenderUtil.drawRect(19.0f, 16.5f, 23 + OctoHack.textManager.getStringWidth(this.module.getDescription()), 32.0f, 0x77000000);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            }
            if (this.subOpen) {
                for (Item item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 11;
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 11;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}
