package me.primooctopus33.octohack.client.modules.movement;

import java.util.Objects;
import java.util.Random;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.ClientEvent;
import me.primooctopus33.octohack.event.events.MoveEvent;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.MathUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroundSpeed
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.INSTANT));
    public Setting<Boolean> strafeJump = this.register(new Setting<Object>("Jump", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.INSTANT));
    public Setting<Boolean> noShake = this.register(new Setting<Object>("NoShake", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.INSTANT));
    public Setting<Boolean> useTimer = this.register(new Setting<Object>("UseTimer", Boolean.valueOf(false), v -> this.mode.getValue() != Mode.INSTANT));
    public Setting<Double> zeroSpeed = this.register(new Setting<Object>("0-Speed", Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(100.0), v -> this.mode.getValue() == Mode.VANILLA));
    public Setting<Double> speed = this.register(new Setting<Object>("Speed", Double.valueOf(10.0), Double.valueOf(0.1), Double.valueOf(100.0), v -> this.mode.getValue() == Mode.VANILLA));
    public Setting<Double> blocked = this.register(new Setting<Object>("Blocked", Double.valueOf(10.0), Double.valueOf(0.0), Double.valueOf(100.0), v -> this.mode.getValue() == Mode.VANILLA));
    public Setting<Double> unblocked = this.register(new Setting<Object>("Unblocked", Double.valueOf(10.0), Double.valueOf(0.0), Double.valueOf(100.0), v -> this.mode.getValue() == Mode.VANILLA));
    private static GroundSpeed INSTANCE = new GroundSpeed();
    private double highChainVal = 0.0;
    private double lowChainVal = 0.0;
    private boolean oneTime = false;
    public double startY = 0.0;
    public boolean antiShake = false;
    private double bounceHeight = 0.4;
    private float move = 0.26f;
    public double minY = 0.0;
    public boolean changeY = false;
    private int vanillaCounter = 0;

    public GroundSpeed() {
        super("GroundSpeed", "Makes you faster on ground", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static GroundSpeed getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroundSpeed();
        }
        return INSTANCE;
    }

    private boolean shouldReturn() {
        return OctoHack.moduleManager.isModuleEnabled("Freecam") || OctoHack.moduleManager.isModuleEnabled("Phase") || OctoHack.moduleManager.isModuleEnabled("ElytraFlight") || OctoHack.moduleManager.isModuleEnabled("Strafe") || OctoHack.moduleManager.isModuleEnabled("Flight");
    }

    @Override
    public void onUpdate() {
        if (this.shouldReturn() || GroundSpeed.mc.player.isSneaking() || GroundSpeed.mc.player.isInWater() || GroundSpeed.mc.player.isInLava()) {
            return;
        }
        switch (this.mode.getValue()) {
            case BOOST: {
                this.doBoost();
                break;
            }
            case ACCEL: {
                this.doAccel();
                break;
            }
            case ONGROUND: {
                this.doOnground();
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() != Mode.VANILLA || GroundSpeed.nullCheck()) {
            return;
        }
        switch (event.getStage()) {
            case 0: {
                int n = this.vanillaCounter = this.vanilla() ? (this.vanillaCounter = this.vanillaCounter + 1) : 0;
                if (this.vanillaCounter != 4) break;
                this.changeY = true;
                this.minY = GroundSpeed.mc.player.getEntityBoundingBox().minY + (GroundSpeed.mc.world.getBlockState(GroundSpeed.mc.player.getPosition()).getMaterial().blocksMovement() ? -this.blocked.getValue().doubleValue() / 10.0 : this.unblocked.getValue() / 10.0) + this.getJumpBoostModifier();
                return;
            }
            case 1: {
                if (this.vanillaCounter == 3) {
                    GroundSpeed.mc.player.motionX *= this.zeroSpeed.getValue() / 10.0;
                    GroundSpeed.mc.player.motionZ *= this.zeroSpeed.getValue() / 10.0;
                    break;
                }
                if (this.vanillaCounter != 4) break;
                GroundSpeed.mc.player.motionX /= this.speed.getValue() / 10.0;
                GroundSpeed.mc.player.motionZ /= this.speed.getValue() / 10.0;
                this.vanillaCounter = 2;
            }
        }
    }

    private double getJumpBoostModifier() {
        double boost = 0.0;
        if (GroundSpeed.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            int amplifier = Objects.requireNonNull(GroundSpeed.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier();
            boost *= 1.0 + 0.2 * (double)amplifier;
        }
        return boost;
    }

    private boolean vanillaCheck() {
        if (GroundSpeed.mc.player.onGround) {
            // empty if block
        }
        return false;
    }

    private boolean vanilla() {
        return GroundSpeed.mc.player.onGround;
    }

    private void doBoost() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (GroundSpeed.mc.player.onGround) {
            this.startY = GroundSpeed.mc.player.posY;
        }
        if (EntityUtil.getEntitySpeed(GroundSpeed.mc.player) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving(GroundSpeed.mc.player) && !GroundSpeed.mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid(GroundSpeed.mc.player) && BlockUtil.isBlockBelowEntitySolid(GroundSpeed.mc.player)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && GroundSpeed.mc.player.getRidingEntity() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (GroundSpeed.mc.player.posY >= this.startY + this.bounceHeight) {
                GroundSpeed.mc.player.motionY = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.15f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.2f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.225f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.25f;
                }
                if (this.lowChainVal >= 7.0) {
                    this.move = 0.27895f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    OctoHack.timerManager.setTimer(1.0f);
                }
            }
            if (GroundSpeed.mc.player.posY == this.startY) {
                GroundSpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.325f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.4f;
                }
                if (this.highChainVal >= 6.0) {
                    this.move = 0.43395f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        OctoHack.timerManager.setTimer(1.3f);
                    } else {
                        OctoHack.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, GroundSpeed.mc.player);
        } else {
            if (this.oneTime) {
                GroundSpeed.mc.player.motionY = -0.1;
                this.oneTime = false;
            }
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.antiShake = false;
            this.speedOff();
        }
    }

    private void doAccel() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (GroundSpeed.mc.player.onGround) {
            this.startY = GroundSpeed.mc.player.posY;
        }
        if (EntityUtil.getEntitySpeed(GroundSpeed.mc.player) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving(GroundSpeed.mc.player) && !GroundSpeed.mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid(GroundSpeed.mc.player) && BlockUtil.isBlockBelowEntitySolid(GroundSpeed.mc.player)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && GroundSpeed.mc.player.getRidingEntity() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (GroundSpeed.mc.player.posY >= this.startY + this.bounceHeight) {
                GroundSpeed.mc.player.motionY = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.275f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.35f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.375f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.4f;
                }
                if (this.lowChainVal == 7.0) {
                    this.move = 0.425f;
                }
                if (this.lowChainVal == 8.0) {
                    this.move = 0.45f;
                }
                if (this.lowChainVal == 9.0) {
                    this.move = 0.475f;
                }
                if (this.lowChainVal == 10.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 11.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 12.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 13.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 14.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 15.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 16.0) {
                    this.move = 0.545f;
                }
                if (this.lowChainVal >= 17.0) {
                    this.move = 0.545f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    OctoHack.timerManager.setTimer(1.0f);
                }
            }
            if (GroundSpeed.mc.player.posY == this.startY) {
                GroundSpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.6f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.775f;
                }
                if (this.highChainVal == 6.0) {
                    this.move = 0.825f;
                }
                if (this.highChainVal == 7.0) {
                    this.move = 0.875f;
                }
                if (this.highChainVal == 8.0) {
                    this.move = 0.925f;
                }
                if (this.highChainVal == 9.0) {
                    this.move = 0.975f;
                }
                if (this.highChainVal == 10.0) {
                    this.move = 1.05f;
                }
                if (this.highChainVal == 11.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 12.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 13.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 14.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 15.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal == 16.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal >= 17.0) {
                    this.move = 1.175f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        OctoHack.timerManager.setTimer(1.3f);
                    } else {
                        OctoHack.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, GroundSpeed.mc.player);
        } else {
            if (this.oneTime) {
                GroundSpeed.mc.player.motionY = -0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff();
        }
    }

    private void doOnground() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (GroundSpeed.mc.player.onGround) {
            this.startY = GroundSpeed.mc.player.posY;
        }
        if (EntityUtil.getEntitySpeed(GroundSpeed.mc.player) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving(GroundSpeed.mc.player) && !GroundSpeed.mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid(GroundSpeed.mc.player) && BlockUtil.isBlockBelowEntitySolid(GroundSpeed.mc.player)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && GroundSpeed.mc.player.getRidingEntity() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (GroundSpeed.mc.player.posY >= this.startY + this.bounceHeight) {
                GroundSpeed.mc.player.motionY = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.275f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.35f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.375f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.4f;
                }
                if (this.lowChainVal == 7.0) {
                    this.move = 0.425f;
                }
                if (this.lowChainVal == 8.0) {
                    this.move = 0.45f;
                }
                if (this.lowChainVal == 9.0) {
                    this.move = 0.475f;
                }
                if (this.lowChainVal == 10.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 11.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 12.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 13.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 14.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 15.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 16.0) {
                    this.move = 0.545f;
                }
                if (this.lowChainVal >= 17.0) {
                    this.move = 0.545f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    OctoHack.timerManager.setTimer(1.0f);
                }
            }
            if (GroundSpeed.mc.player.posY == this.startY) {
                GroundSpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.6f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.775f;
                }
                if (this.highChainVal == 6.0) {
                    this.move = 0.825f;
                }
                if (this.highChainVal == 7.0) {
                    this.move = 0.875f;
                }
                if (this.highChainVal == 8.0) {
                    this.move = 0.925f;
                }
                if (this.highChainVal == 9.0) {
                    this.move = 0.975f;
                }
                if (this.highChainVal == 10.0) {
                    this.move = 1.05f;
                }
                if (this.highChainVal == 11.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 12.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 13.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 14.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 15.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal == 16.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal >= 17.0) {
                    this.move = 1.2f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        OctoHack.timerManager.setTimer(1.3f);
                    } else {
                        OctoHack.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, GroundSpeed.mc.player);
        } else {
            if (this.oneTime) {
                GroundSpeed.mc.player.motionY = -0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff();
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.ONGROUND || this.mode.getValue() == Mode.BOOST) {
            GroundSpeed.mc.player.motionY = -0.1;
        }
        this.changeY = false;
        OctoHack.timerManager.setTimer(1.0f);
        this.highChainVal = 0.0;
        this.lowChainVal = 0.0;
        this.antiShake = false;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().equals(this.mode) && this.mode.getPlannedValue() == Mode.INSTANT) {
            GroundSpeed.mc.player.motionY = -0.1;
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (!(this.shouldReturn() || event.getStage() != 0 || this.mode.getValue() != Mode.INSTANT || GroundSpeed.nullCheck() || GroundSpeed.mc.player.isSneaking() || GroundSpeed.mc.player.isInWater() || GroundSpeed.mc.player.isInLava() || GroundSpeed.mc.player.movementInput.moveForward == 0.0f && GroundSpeed.mc.player.movementInput.moveStrafe == 0.0f)) {
            if (GroundSpeed.mc.player.onGround && this.strafeJump.getValue().booleanValue()) {
                GroundSpeed.mc.player.motionY = 0.4;
                event.setY(0.4);
            }
            MovementInput movementInput = GroundSpeed.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = GroundSpeed.mc.player.rotationYaw;
            if ((double)moveForward == 0.0 && (double)moveStrafe == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else {
                if ((double)moveForward != 0.0) {
                    float f;
                    if ((double)moveStrafe > 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? -45 : 45);
                    } else if ((double)moveStrafe < 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    float f2 = moveForward == 0.0f ? moveForward : (f = (moveForward = (double)moveForward > 0.0 ? 1.0f : -1.0f));
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double)moveStrafe > 0.0 ? 1.0f : -1.0f);
                event.setX((double)moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ((double)moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    private void speedOff() {
        float yaw = (float)Math.toRadians(GroundSpeed.mc.player.rotationYaw);
        if (BlockUtil.isBlockAboveEntitySolid(GroundSpeed.mc.player)) {
            if (GroundSpeed.mc.gameSettings.keyBindForward.isKeyDown() && !GroundSpeed.mc.gameSettings.keyBindSneak.isKeyDown() && GroundSpeed.mc.player.onGround) {
                GroundSpeed.mc.player.motionX -= (double)MathUtil.sin(yaw) * 0.15;
                GroundSpeed.mc.player.motionZ += (double)MathUtil.cos(yaw) * 0.15;
            }
        } else if (GroundSpeed.mc.player.collidedHorizontally) {
            if (GroundSpeed.mc.gameSettings.keyBindForward.isKeyDown() && !GroundSpeed.mc.gameSettings.keyBindSneak.isKeyDown() && GroundSpeed.mc.player.onGround) {
                GroundSpeed.mc.player.motionX -= (double)MathUtil.sin(yaw) * 0.03;
                GroundSpeed.mc.player.motionZ += (double)MathUtil.cos(yaw) * 0.03;
            }
        } else if (!BlockUtil.isBlockBelowEntitySolid(GroundSpeed.mc.player)) {
            if (GroundSpeed.mc.gameSettings.keyBindForward.isKeyDown() && !GroundSpeed.mc.gameSettings.keyBindSneak.isKeyDown() && GroundSpeed.mc.player.onGround) {
                GroundSpeed.mc.player.motionX -= (double)MathUtil.sin(yaw) * 0.03;
                GroundSpeed.mc.player.motionZ += (double)MathUtil.cos(yaw) * 0.03;
            }
        } else {
            GroundSpeed.mc.player.motionX = 0.0;
            GroundSpeed.mc.player.motionZ = 0.0;
        }
    }

    public static enum Mode {
        INSTANT,
        ONGROUND,
        ACCEL,
        BOOST,
        VANILLA;

    }
}
