package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.Module;

public class AutoStairJump
extends Module {
    public AutoStairJump() {
        super("AutoStairJump", "Automatically Jumps when you hit a stair to make you go up faster", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (AutoStairJump.mc.player.onGround && AutoStairJump.mc.player.posY - Math.floor(AutoStairJump.mc.player.posY) > 0.0 && AutoStairJump.mc.player.moveForward != 0.0f) {
            AutoStairJump.mc.player.jump();
        }
    }
}
