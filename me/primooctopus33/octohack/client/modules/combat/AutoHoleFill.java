package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.HoleUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoHoleFill
extends Module {
    public final Setting<Boolean> matCheck = this.register(new Setting<Boolean>("Material Check", true));
    public final Setting<Boolean> doubleHoles = this.register(new Setting<Boolean>("Fill Double Holes", true));
    public final Setting<Sensitivity> sensitivity = this.register(new Setting<Sensitivity>("Sensitivity", Sensitivity.Less));
    public final Setting<Integer> validHoleHeight = this.register(new Setting<Integer>("Valid Hole Height", 2, 1, 5));
    public final Setting<Integer> bps = this.register(new Setting<Integer>("Blocks Per Tick", 3, 1, 8));
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 1, 10));
    public final Setting<Float> validPlayerRange = this.register(new Setting<Float>("Valid Player Range", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(15.0f)));
    public final Setting<Boolean> toggle = this.register(new Setting<Boolean>("Disables", false));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> smart = this.register(new Setting<Boolean>("Smart", true));
    public final Setting<Integer> distance = this.register(new Setting<Integer>("Smart Range", 3, 1, 5));
    public final Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    public final Setting<Integer> ticks = this.register(new Setting<Integer>("Predict Delay", 3, 1, 5));

    public AutoHoleFill() {
        super("AutoHoleFill", "Automatically Fills safe holes near the opponent for Crystal PvP", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (AutoHoleFill.nullCheck()) {
            return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void onUpdate() {
        if (AutoHoleFill.nullCheck()) {
            return;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int anvilSlot = InventoryUtil.findHotbarBlock(BlockAnvil.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        int old_slot = -1;
        int oldSlot = AutoHoleFill.mc.player.inventory.currentItem;
        int placed = 0;
        ArrayList<HoleUtil.Hole> holeList = HoleUtil.holes(this.range.getValue().intValue(), this.validHoleHeight.getValue());
        if (this.smart.getValue().booleanValue()) {
            EntityPlayer target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, this.validPlayerRange.getValue().floatValue(), EntityUtil.toMode("Closest"));
            if (target == null) {
                return;
            }
            Vec3d vec = this.predict.getValue() != false ? target.getPositionVector() : MathUtil.extrapolatePlayerPosition(target, (int)this.ticks.getValue());
            holeList.removeIf(e -> {
                if (e instanceof HoleUtil.SingleHole) {
                    return vec.squareDistanceTo(new Vec3d(((HoleUtil.SingleHole)e).pos).addVector(0.5, 0.5, 0.5)) >= (double)(this.distance.getValue() * this.distance.getValue());
                }
                HoleUtil.Hole hole = HoleUtil.getHole(EntityUtil.getEntityPosFloored(target), 1);
                if (hole instanceof HoleUtil.DoubleHole && holeList.contains(hole)) {
                    return true;
                }
                Vec3d vec3d = new Vec3d(((HoleUtil.DoubleHole)e).pos);
                if (vec.squareDistanceTo(vec3d.addVector(0.5, 0.5, 0.5)) >= (double)(this.distance.getValue() * this.distance.getValue())) {
                    return true;
                }
                return vec.squareDistanceTo(new Vec3d(((HoleUtil.DoubleHole)e).pos1).addVector(0.5, 0.5, 0.5)) >= (double)(this.distance.getValue() * this.distance.getValue());
            });
        }
        if (holeList.isEmpty()) {
            return;
        }
        for (HoleUtil.Hole hole : holeList) {
            if (this.smart.getValue().booleanValue()) {
                EntityPlayer target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
                if (placed >= this.bps.getValue()) continue;
                if (hole instanceof HoleUtil.SingleHole && !EntityUtil.isInHole(target) && WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtil.SingleHole)hole).pos)) && (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1 || InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) && !BlockUtil.isIntercepted(((HoleUtil.SingleHole)hole).pos)) {
                    if (this.sensitivity.getValue() == Sensitivity.Less) {
                        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                        } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                        } else {
                            if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                        }
                    } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                        if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                        }
                    } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                        if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                        }
                    } else {
                        if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                        if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                        }
                    }
                    BlockUtil.placeBlockss(((HoleUtil.SingleHole)hole).pos, false, this.packet.getValue(), this.rotate.getValue());
                    if (this.sensitivity.getValue() == Sensitivity.Less) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                        AutoHoleFill.mc.player.inventory.currentItem = old_slot;
                    }
                    ++placed;
                }
                if (placed >= this.bps.getValue() || !(hole instanceof HoleUtil.DoubleHole) || !this.doubleHoles.getValue().booleanValue() || placed >= this.bps.getValue()) continue;
                HoleUtil.DoubleHole doubleH = (HoleUtil.DoubleHole)hole;
                if (!(!this.getDist(doubleH.pos) || EntityUtil.isInHole(target) || BlockUtil.isInterceptedByOther(doubleH.pos) || !WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos)) || InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1 && InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1 || BlockUtil.isIntercepted(doubleH.pos))) {
                    if (this.sensitivity.getValue() == Sensitivity.Less) {
                        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                        } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                        } else {
                            if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                            AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                        }
                    } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                        if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                        }
                    } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                        if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                        }
                    } else {
                        if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                        if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                            old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                            AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                        }
                    }
                    BlockUtil.placeBlockss(doubleH.pos, false, this.packet.getValue(), this.rotate.getValue());
                    if (this.sensitivity.getValue() == Sensitivity.Less) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                        AutoHoleFill.mc.player.inventory.currentItem = old_slot;
                    }
                    ++placed;
                }
                if (placed >= this.bps.getValue() || !this.getDist(doubleH.pos1) || EntityUtil.isInHole(target) || BlockUtil.isInterceptedByOther(doubleH.pos1) || !WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos1)) || InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1 && InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1 || BlockUtil.isIntercepted(doubleH.pos1)) continue;
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                    } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                    } else {
                        if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                    if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                    }
                } else {
                    if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                    if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                    }
                }
                BlockUtil.placeBlockss(doubleH.pos1, false, this.packet.getValue(), this.rotate.getValue());
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                    AutoHoleFill.mc.player.inventory.currentItem = old_slot;
                }
                ++placed;
                continue;
            }
            if (placed >= this.bps.getValue()) continue;
            if (hole instanceof HoleUtil.SingleHole && WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtil.SingleHole)hole).pos)) && !BlockUtil.isIntercepted(((HoleUtil.SingleHole)hole).pos)) {
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                    } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                    } else {
                        if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                    if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                    }
                } else {
                    if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                    if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                    }
                }
                BlockUtil.placeBlockss(((HoleUtil.SingleHole)hole).pos, false, this.packet.getValue(), this.rotate.getValue());
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                    AutoHoleFill.mc.player.inventory.currentItem = old_slot;
                }
                ++placed;
            }
            if (placed >= this.bps.getValue() || !(hole instanceof HoleUtil.DoubleHole) || !this.doubleHoles.getValue().booleanValue() || placed >= this.bps.getValue()) continue;
            HoleUtil.DoubleHole doubleH = (HoleUtil.DoubleHole)hole;
            if (this.getDist(doubleH.pos) && !BlockUtil.isInterceptedByOther(doubleH.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos)) && !BlockUtil.isIntercepted(doubleH.pos)) {
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                    } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                    } else {
                        if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                        AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                    }
                } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                    if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                    }
                } else {
                    if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                    if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                        old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                        AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                    }
                }
                BlockUtil.placeBlockss(doubleH.pos, false, this.packet.getValue(), this.rotate.getValue());
                if (this.sensitivity.getValue() == Sensitivity.Less) {
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                    AutoHoleFill.mc.player.inventory.currentItem = old_slot;
                }
                ++placed;
            }
            if (placed >= this.bps.getValue() || !this.getDist(doubleH.pos1) || BlockUtil.isInterceptedByOther(doubleH.pos1) || !WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos1)) || BlockUtil.isIntercepted(doubleH.pos1)) continue;
            if (this.sensitivity.getValue() == Sensitivity.Less) {
                if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(obbySlot));
                } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(anvilSlot));
                } else {
                    if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                    AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(echestSlot));
                }
            } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                if (obbySlot != AutoHoleFill.mc.player.inventory.currentItem) {
                    old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                    AutoHoleFill.mc.player.inventory.currentItem = obbySlot;
                }
            } else if (InventoryUtil.findHotbarBlock(BlockAnvil.class) != -1) {
                if (anvilSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                    old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                    AutoHoleFill.mc.player.inventory.currentItem = anvilSlot;
                }
            } else {
                if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) == -1) return;
                if (echestSlot != AutoHoleFill.mc.player.inventory.currentItem) {
                    old_slot = AutoHoleFill.mc.player.inventory.currentItem;
                    AutoHoleFill.mc.player.inventory.currentItem = echestSlot;
                }
            }
            BlockUtil.placeBlockss(doubleH.pos1, false, this.packet.getValue(), this.rotate.getValue());
            if (this.sensitivity.getValue() == Sensitivity.Less) {
                AutoHoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            } else if (this.sensitivity.getValue() == Sensitivity.Strong && old_slot != -1) {
                AutoHoleFill.mc.player.inventory.currentItem = old_slot;
            }
            ++placed;
        }
        if (placed != 0 || !holeList.isEmpty() || !this.toggle.getValue().booleanValue()) return;
        Command.sendMessage("Finished Holefilling, disabling!");
        this.disable();
    }

    private boolean getDist(BlockPos pos) {
        if (AutoHoleFill.nullCheck() || pos == null) {
            return false;
        }
        return pos.add(0.5, 0.5, 0.5).distanceSq(AutoHoleFill.mc.player.posX, AutoHoleFill.mc.player.posY + (double)AutoHoleFill.mc.player.eyeHeight, AutoHoleFill.mc.player.posZ) < Math.pow(this.range.getValue().intValue(), 2.0);
    }

    public static enum Sensitivity {
        Strong,
        Less;

    }
}
