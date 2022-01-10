package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class CevBreaker
extends Module {
    public final Setting<Double> range = this.register(new Setting<Double>("Range", 4.5, 0.0, 7.0));
    public final Setting<Integer> blocksPerPlace = this.register(new Setting<Integer>("Blocks Per Place", 4, 1, 20));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Place Delay MS", 50, 1, 400));
    public final Setting<CrystalHand> crystalHand = this.register(new Setting<CrystalHand>("Crystal Hand", CrystalHand.HOTBAR));
    public final Setting<TrapMode> structure = this.register(new Setting<TrapMode>("Trap Mode", TrapMode.ANTISTEP));
    public final Setting<MineMode> breakMode = this.register(new Setting<MineMode>("Mine Mode", MineMode.INSTANT));
    public final Setting<Boolean> oneFifteen = this.register(new Setting<Boolean>("1.15", false));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> render = this.register(new Setting<Boolean>("Render Block", false));
    public final Setting<Integer> breakdelay = this.register(new Setting<Integer>("Crystal Break Delay", 1, 0, 10));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", Boolean.valueOf(true), v -> this.render.getValue()));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 155, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 125, 0, 255));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Vec3d[] offsetTopOnly = new Vec3d[]{new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0)};
    private final Vec3d[] offsetsFullTrap = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
    private final Vec3d[] offsetsTrapProtected = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(-1.0, 4.0, 0.0), new Vec3d(0.0, 4.0, 1.0), new Vec3d(0.0, 4.0, -1.0)};
    private final Vec3d[] offsetsTrapProtectedOneFifteen = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(-1.0, 4.0, 0.0), new Vec3d(0.0, 4.0, 1.0), new Vec3d(0.0, 4.0, -1.0), new Vec3d(0.0, 5.0, 1.0), new Vec3d(0.0, 5.0, 0.0)};

    public CevBreaker() {
        super("CevBreaker", "Places a crystal on the opponent's head, instantmines the top block and explodes the Crystal", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (CevBreaker.nullCheck()) {
            return;
        }
        EntityPlayer target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
    }

    public static enum step {
        Trapping,
        Breaking,
        Explode;

    }

    public static enum MineMode {
        INSTANT,
        NORMAL,
        PACKET;

    }

    public static enum SwingMode {
        MAINHAND,
        OFFHAND,
        NONE;

    }

    public static enum TrapMode {
        NOOBBYWASTE,
        FULLTRAP,
        ANTISTEP;

    }

    public static enum CrystalHand {
        HOTBAR,
        OFFHAND;

    }
}
