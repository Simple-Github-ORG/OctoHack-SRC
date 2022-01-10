package me.primooctopus33.octohack.client.modules.combat;

import java.awt.Color;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CCBurrowBypass
extends Module {
    public final Setting<Float> timerMultiplier = this.register(new Setting<Float>("Timer Multiplier", Float.valueOf(2.5f), Float.valueOf(0.0f), Float.valueOf(50.0f)));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> packetPlace = this.register(new Setting<Boolean>("Packet Place", true));
    public final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    public final Setting<Boolean> colorSync = this.register(new Setting<Object>("Sync", Boolean.valueOf(false), v -> this.render.getValue()));
    public final Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(false), v -> this.render.getValue()));
    public final Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Boolean> customOutline = this.register(new Setting<Object>("Custom Line", Boolean.valueOf(false), v -> this.outline.getValue() != false && this.render.getValue() != false));
    public final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> boxAlpha = this.register(new Setting<Object>("Box Alpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.render.getValue() != false));
    public final Setting<Float> lineWidth = this.register(new Setting<Object>("Line Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue() != false && this.render.getValue() != false));
    public final Setting<Integer> cRed = this.register(new Setting<Object>("Line Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    public final Setting<Integer> cGreen = this.register(new Setting<Object>("Line Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    public final Setting<Integer> cBlue = this.register(new Setting<Object>("Line Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    public final Setting<Integer> cAlpha = this.register(new Setting<Object>("Line Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.render.getValue() != false));
    public BlockPos playerPos = null;
    public Timer timer = new Timer();

    public CCBurrowBypass() {
        super("CCBurrowBypass", "Burrow bypass for Crystalpvp.cc", Module.Category.COMBAT, true, false, false);
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        this.playerPos = new BlockPos(CCBurrowBypass.mc.player.posX, CCBurrowBypass.mc.player.posY - 1.0, CCBurrowBypass.mc.player.posZ);
        RenderUtil.drawBoxESP(this.playerPos, this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
    }

    @Override
    public void onUpdate() {
        this.playerPos = new BlockPos(CCBurrowBypass.mc.player.posX, CCBurrowBypass.mc.player.posY - 1.0, CCBurrowBypass.mc.player.posZ);
        int obbySlot = InventoryUtil.find(Items.END_CRYSTAL);
        int oldSlot = CCBurrowBypass.mc.player.inventory.currentItem;
        CCBurrowBypass.mc.gameSettings.keyBindJump.pressed = true;
        CCBurrowBypass.mc.timer.tickLength = 50.0f / this.timerMultiplier.getValue().floatValue();
        CCBurrowBypass.mc.player.inventory.currentItem = obbySlot;
        BlockUtil.placeBlock(this.playerPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), true, true);
        CCBurrowBypass.mc.player.inventory.currentItem = oldSlot;
        CCBurrowBypass.mc.gameSettings.keyBindJump.pressed = false;
        CCBurrowBypass.mc.player.motionY -= 0.4;
        CCBurrowBypass.mc.timer.tickLength = 50.0f;
        this.disable();
    }
}
