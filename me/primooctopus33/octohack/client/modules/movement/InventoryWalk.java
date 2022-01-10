package me.primooctopus33.octohack.client.modules.movement;

import java.util.function.Predicate;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.event.events.GuiScreenEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

public class InventoryWalk
extends Module {
    private static final KeyBinding[] MOVEMENT_KEYS = new KeyBinding[]{InventoryWalk.mc.gameSettings.keyBindForward, InventoryWalk.mc.gameSettings.keyBindRight, InventoryWalk.mc.gameSettings.keyBindBack, InventoryWalk.mc.gameSettings.keyBindLeft, InventoryWalk.mc.gameSettings.keyBindJump, InventoryWalk.mc.gameSettings.keyBindSprint};
    @EventHandler
    public Listener<GuiScreenEvent.Displayed> listener = new Listener<GuiScreenEvent.Displayed>(event -> {
        if (InventoryWalk.mc.currentScreen == null) {
            return;
        }
        if (InventoryWalk.mc.currentScreen instanceof GuiChat) {
            return;
        }
        this.runCheck();
    }, new Predicate[0]);

    public InventoryWalk() {
        super("InventoryWalk", "Allows you to Move while in GUIs", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (InventoryWalk.mc.currentScreen == null) {
            return;
        }
        if (InventoryWalk.mc.currentScreen instanceof GuiChat) {
            return;
        }
        InventoryWalk.mc.player.rotationYaw = InventoryWalk.mc.player.rotationYaw + (Keyboard.isKeyDown(205) ? 4.0f : (Keyboard.isKeyDown(203) ? -4.0f : 0.0f));
        InventoryWalk.mc.player.rotationPitch = (float)((double)InventoryWalk.mc.player.rotationPitch + (double)(Keyboard.isKeyDown(208) ? 4 : (Keyboard.isKeyDown(200) ? -4 : 0)) * 0.75);
        InventoryWalk.mc.player.rotationPitch = MathHelper.clamp((float)InventoryWalk.mc.player.rotationPitch, (float)-90.0f, (float)90.0f);
        this.runCheck();
    }

    private void runCheck() {
        for (KeyBinding keyBinding : MOVEMENT_KEYS) {
            if (Keyboard.isKeyDown(keyBinding.getKeyCode())) {
                if (keyBinding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
                    keyBinding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
                }
                KeyBinding.setKeyBindState((int)keyBinding.getKeyCode(), (boolean)true);
                continue;
            }
            KeyBinding.setKeyBindState((int)keyBinding.getKeyCode(), (boolean)false);
        }
    }
}
