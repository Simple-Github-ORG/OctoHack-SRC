package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.gui.OctoHackGui;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.movement.Velocity;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiSlowDown
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Normal));
    private final Setting<Boolean> guiMove = this.register(new Setting<Boolean>("GuiTurn", true));
    boolean sneaking;
    private static final KeyBinding[] keys = new KeyBinding[]{Velocity.mc.gameSettings.keyBindForward, Velocity.mc.gameSettings.keyBindBack, Velocity.mc.gameSettings.keyBindLeft, Velocity.mc.gameSettings.keyBindRight, Velocity.mc.gameSettings.keyBindJump, Velocity.mc.gameSettings.keyBindSprint};

    public AntiSlowDown() {
        super("AntiSlowDown", "Stops you from getting slowed down when performing various actions", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (Velocity.nullCheck()) {
            return;
        }
        if (this.guiMove.getValue().booleanValue()) {
            for (KeyBinding bind : keys) {
                KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)Keyboard.isKeyDown(bind.getKeyCode()));
            }
            if (Velocity.mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)false);
                }
            }
        }
        if (this.mode.getValue() == Mode.Strict) {
            Item item = Velocity.mc.player.getActiveItemStack().getItem();
            if (this.sneaking && (!Velocity.mc.player.isHandActive() && item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion || !(item instanceof ItemFood) || !(item instanceof ItemBow) || !(item instanceof ItemPotion))) {
                Velocity.mc.player.connection.sendPacket(new CPacketEntityAction(Velocity.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }
        }
        if (Velocity.mc.currentScreen != null && !(Velocity.mc.currentScreen instanceof GuiChat) && this.guiMove.getValue().booleanValue()) {
            if (Velocity.mc.currentScreen instanceof OctoHackGui && !this.guiMove.getValue().booleanValue()) {
                return;
            }
            if (Keyboard.isKeyDown(200)) {
                Velocity.mc.player.rotationPitch -= 5.0f;
            }
            if (Keyboard.isKeyDown(208)) {
                Velocity.mc.player.rotationPitch += 5.0f;
            }
            if (Keyboard.isKeyDown(205)) {
                Velocity.mc.player.rotationYaw += 5.0f;
            }
            if (Keyboard.isKeyDown(203)) {
                Velocity.mc.player.rotationYaw -= 5.0f;
            }
            if (Velocity.mc.player.rotationPitch > 90.0f) {
                Velocity.mc.player.rotationPitch = 90.0f;
            }
            if (Velocity.mc.player.rotationPitch < -90.0f) {
                Velocity.mc.player.rotationPitch = -90.0f;
            }
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent event) {
        if (this.mode.getValue() == Mode.Strict && !this.sneaking) {
            Velocity.mc.player.connection.sendPacket(new CPacketEntityAction(Velocity.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (this.mode.getValue() == Mode.Normal && Velocity.mc.player.isHandActive() && !Velocity.mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    public static enum Mode {
        Normal,
        Strict;

    }
}
