package me.primooctopus33.octohack.client.modules.player;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

public class MCP
extends Module {
    private boolean clickedbutton = false;
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MiddleClick));

    public MCP() {
        super("MCP", "Automatically throws an ender pearl when you middle click", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        if (!MCP.fullNullCheck() && this.mode.getValue() == Mode.Toggle) {
            this.throwPearl();
            this.disable();
        }
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Mode.MiddleClick) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clickedbutton) {
                    this.throwPearl();
                }
                this.clickedbutton = true;
            } else {
                this.clickedbutton = false;
            }
        }
    }

    private void throwPearl() {
        Entity entity;
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        RayTraceResult result = MCP.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            return;
        }
        boolean offhand = MCP.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        boolean bl = offhand;
        if (pearlSlot != -1 || offhand) {
            int oldslot = MCP.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            MCP.mc.playerController.processRightClick(MCP.mc.player, MCP.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public static enum Mode {
        Toggle,
        MiddleClick;

    }
}
