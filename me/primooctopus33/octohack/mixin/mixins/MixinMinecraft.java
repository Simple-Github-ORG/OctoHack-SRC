package me.primooctopus33.octohack.mixin.mixins;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.event.events.KeyEvent;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public abstract class MixinMinecraft {
    private long lastFrame = this.getTime();

    @Inject(method={"shutdownMinecraftApplet"}, at={@At(value="HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        this.unload();
    }

    @Redirect(method={"run"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.unload();
    }

    @Inject(method={"runTickKeyboard"}, at={@At(value="INVOKE", remap=false, target="Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal=0, shift=At.Shift.BEFORE)})
    private void onKeyboard(CallbackInfo callbackInfo) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        int n = i;
        if (Keyboard.getEventKeyState()) {
            KeyEvent event = new KeyEvent(i);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }

    public long getTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="HEAD")})
    private void onRunGameLoopPre(CallbackInfo ci) {
        long currentTime = this.getTime();
        int deltaTime = (int)(currentTime - this.lastFrame);
        this.lastFrame = currentTime;
        RenderUtil.deltaTime = deltaTime;
    }

    private void unload() {
        OctoHack.LOGGER.info("Initiated client shutdown.");
        OctoHack.onUnload();
        OctoHack.LOGGER.info("Finished client shutdown.");
    }
}
