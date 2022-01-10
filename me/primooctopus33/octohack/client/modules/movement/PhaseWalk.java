package me.primooctopus33.octohack.client.modules.movement;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class PhaseWalk
extends Module {
    public final Setting<Boolean> phaseCheck = this.register(new Setting<Boolean>("Only In Block", true));
    public final Setting<NoClipMode> noClipMode = this.register(new Setting<NoClipMode>("NoClipMode", NoClipMode.NoClip));
    public final Setting<Boolean> fallPacket = this.register(new Setting<Boolean>("Fall Packet", true));
    public final Setting<Boolean> sprintPacket = this.register(new Setting<Boolean>("Sprint Packet", true));
    public final Setting<Boolean> instantWalk = this.register(new Setting<Boolean>("Instant Walk", true));
    public final Setting<Boolean> selfAnvil = this.register(new Setting<Boolean>("Self Anvil", false));
    public final Setting<Boolean> antiVoid = this.register(new Setting<Boolean>("Anti Void", false));
    public final Setting<Boolean> clip = this.register(new Setting<Boolean>("Clip", true));
    public final Setting<Boolean> clipRange = this.register(new Setting<Integer>("Clip Range", 2, 1, 5));
    public final Setting<Boolean> clipDelay = this.register(new Setting<Integer>("Clip Delay", 10, 1, 150));
    public final Setting<Integer> antiVoidHeight = this.register(new Setting<Integer>("Anti Void Height", 5, 1, 100));
    public final Setting<Double> instantWalkSpeed = this.register(new Setting<Object>("Instant Speed", Double.valueOf(1.8), Double.valueOf(0.1), Double.valueOf(2.0), v -> this.instantWalk.getValue()));
    public final Setting<Double> phaseSpeed = this.register(new Setting<Double>("Phase Walk Speed", 42.4, 0.1, 70.0));
    public final Setting<Boolean> downOnShift = this.register(new Setting<Boolean>("Phase Down When Crouch", true));
    public final Setting<Boolean> stopMotion = this.register(new Setting<Boolean>("Attempt Clips", true));
    public final Setting<Integer> stopMotionDelay = this.register(new Setting<Object>("Attempt Clips Delay", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(20), v -> this.stopMotion.getValue()));
    int delay = 0;

    public PhaseWalk() {
        super("PhaseWalk", "Allows you to walk through blocks", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        double[] dir;
        double[] dirSpeed;
        RayTraceResult trace;
        ++this.delay;
        double phaseSpeedValue = this.phaseSpeed.getValue() / 1000.0;
        double instantSpeedValue = this.instantWalkSpeed.getValue() / 10.0;
        if (this.antiVoid.getValue().booleanValue() && PhaseWalk.mc.player.posY <= (double)this.antiVoidHeight.getValue().intValue() && ((trace = PhaseWalk.mc.world.rayTraceBlocks(PhaseWalk.mc.player.getPositionVector(), new Vec3d(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ), false, false, false)) == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)) {
            PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        if (this.phaseCheck.getValue().booleanValue()) {
            if ((PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) && (!this.eChestCheck() && !PhaseWalk.mc.world.getBlockState(PlayerUtil.getPlayerPos()).getBlock().equals(Blocks.AIR) || !PhaseWalk.mc.world.getBlockState(PlayerUtil.getPlayerPos().up()).getBlock().equals(Blocks.AIR))) {
                double[] speed;
                if (PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isPressed() && PhaseWalk.mc.player.isSneaking()) {
                    dirSpeed = this.getMotion(phaseSpeedValue);
                    if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    } else {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    if (this.noClipMode.getValue() == NoClipMode.Fall) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                    }
                    if (this.noClipMode.getValue() == NoClipMode.NoClip) {
                        PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                        if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                            speed = MathUtil.directionSpeed(0.06f);
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + speed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + speed[1], PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                    }
                    if (this.noClipMode.getValue() == NoClipMode.Bypass) {
                        PhaseWalk.mc.player.noClip = true;
                    }
                    if (this.fallPacket.getValue().booleanValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                    }
                    if (this.sprintPacket.getValue().booleanValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                    if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                    } else {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                    }
                    PhaseWalk.mc.player.motionZ = 0.0;
                    PhaseWalk.mc.player.motionY = 0.0;
                    PhaseWalk.mc.player.motionX = 0.0;
                    PhaseWalk.mc.player.noClip = true;
                }
                if (!PhaseWalk.mc.player.collidedHorizontally || !this.clip.getValue().booleanValue() || PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                    // empty if block
                }
                if (PhaseWalk.mc.player.collidedHorizontally && this.stopMotion.getValue() != false ? this.delay >= this.stopMotionDelay.getValue() : PhaseWalk.mc.player.collidedHorizontally) {
                    dirSpeed = this.getMotion(phaseSpeedValue);
                    if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    } else {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    if (this.noClipMode.getValue() == NoClipMode.Fall) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                    }
                    if (this.noClipMode.getValue() == NoClipMode.NoClip) {
                        PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                        if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                            speed = MathUtil.directionSpeed(0.06f);
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + speed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + speed[1], PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                    }
                    if (this.noClipMode.getValue() == NoClipMode.Bypass) {
                        PhaseWalk.mc.player.noClip = true;
                    }
                    if (this.fallPacket.getValue().booleanValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                    }
                    if (this.sprintPacket.getValue().booleanValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                    if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                    } else {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                    }
                    PhaseWalk.mc.player.motionZ = 0.0;
                    PhaseWalk.mc.player.motionY = 0.0;
                    PhaseWalk.mc.player.motionX = 0.0;
                    PhaseWalk.mc.player.noClip = true;
                    this.delay = 0;
                } else if (this.instantWalk.getValue().booleanValue()) {
                    dir = MathUtil.directionSpeed(instantSpeedValue);
                    PhaseWalk.mc.player.motionX = dir[0];
                    PhaseWalk.mc.player.motionZ = dir[1];
                }
            }
        } else if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
            double[] speed;
            if (PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isPressed() && PhaseWalk.mc.player.isSneaking()) {
                dirSpeed = this.getMotion(phaseSpeedValue);
                if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                } else {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                if (this.noClipMode.getValue() == NoClipMode.Fall) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                }
                if (this.noClipMode.getValue() == NoClipMode.NoClip) {
                    PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                    if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                        speed = MathUtil.directionSpeed(0.06f);
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + speed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + speed[1], PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                }
                if (this.noClipMode.getValue() == NoClipMode.Bypass) {
                    PhaseWalk.mc.player.noClip = true;
                }
                if (this.fallPacket.getValue().booleanValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                }
                if (this.sprintPacket.getValue().booleanValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                } else {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                }
                PhaseWalk.mc.player.motionZ = 0.0;
                PhaseWalk.mc.player.motionY = 0.0;
                PhaseWalk.mc.player.motionX = 0.0;
                PhaseWalk.mc.player.noClip = true;
            }
            if (PhaseWalk.mc.player.collidedHorizontally && this.stopMotion.getValue() != false ? this.delay >= this.stopMotionDelay.getValue() : PhaseWalk.mc.player.collidedHorizontally) {
                dirSpeed = this.getMotion(phaseSpeedValue);
                if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                } else {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                if (this.noClipMode.getValue() == NoClipMode.Fall) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                }
                if (this.noClipMode.getValue() == NoClipMode.NoClip) {
                    PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                    if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                        speed = MathUtil.directionSpeed(0.06f);
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + speed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + speed[1], PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + (double)0.06f, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                }
                if (this.noClipMode.getValue() == NoClipMode.Bypass) {
                    PhaseWalk.mc.player.noClip = true;
                }
                if (this.fallPacket.getValue().booleanValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                }
                if (this.sprintPacket.getValue().booleanValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (this.downOnShift.getValue().booleanValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                } else {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + dirSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + dirSpeed[1]);
                }
                PhaseWalk.mc.player.motionZ = 0.0;
                PhaseWalk.mc.player.motionY = 0.0;
                PhaseWalk.mc.player.motionX = 0.0;
                PhaseWalk.mc.player.noClip = true;
                this.delay = 0;
            } else if (this.instantWalk.getValue().booleanValue()) {
                dir = MathUtil.directionSpeed(instantSpeedValue);
                PhaseWalk.mc.player.motionX = dir[0];
                PhaseWalk.mc.player.motionZ = dir[1];
            }
        }
    }

    private double[] getMotion(double speed) {
        float moveForward = PhaseWalk.mc.player.movementInput.moveForward;
        float moveStrafe = PhaseWalk.mc.player.movementInput.moveStrafe;
        float rotationYaw = PhaseWalk.mc.player.prevRotationYaw + (PhaseWalk.mc.player.rotationYaw - PhaseWalk.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    private double[] getDistance() {
        float forward = PhaseWalk.mc.player.movementInput.moveForward;
        float strafe = PhaseWalk.mc.player.movementInput.moveStrafe;
        float rotYaw = PhaseWalk.mc.player.prevRotationYaw + (PhaseWalk.mc.player.rotationYaw - PhaseWalk.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        double posX = (double)strafe * (double)forward * -Math.sin(Math.toRadians(rotYaw)) + (double)strafe * (double)forward * Math.cos(Math.toRadians(rotYaw));
        double posZ = (double)strafe * (double)forward * Math.cos(Math.toRadians(rotYaw)) - (double)strafe * (double)forward * -Math.sin(Math.toRadians(rotYaw));
        return new double[]{posX, posZ};
    }

    @Override
    public void onDisable() {
        PhaseWalk.mc.player.noClip = false;
    }

    private boolean eChestCheck() {
        String loc = String.valueOf(PhaseWalk.mc.player.posY);
        String deciaml = loc.split("\\.")[1];
        return deciaml.equals("875");
    }

    public static enum NoClipMode {
        NoClip,
        Fall,
        Bypass,
        None;

    }
}
