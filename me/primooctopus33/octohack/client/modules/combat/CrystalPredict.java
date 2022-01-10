package me.primooctopus33.octohack.client.modules.combat;

import java.util.concurrent.TimeUnit;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.Util;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalPredict
extends Module {
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private boolean rotating;
    private int rotationPacketsSpoofed;
    private int highestID = -100000;
    public Setting<Integer> rotations = this.register(new Setting<Integer>("Spoofs", 1, 1, 20));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Boolean> render = this.register(new Setting<Boolean>("Render", false));
    public Setting<Boolean> antiIllegal = this.register(new Setting<Boolean>("Anti Illegal", true));
    public Setting<Boolean> checkPos = this.register(new Setting<Boolean>("CheckPos", false));
    public Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", true));
    public Setting<Boolean> entitycheck = this.register(new Setting<Boolean>("EntityCheck", false));
    public Setting<Integer> attacks = this.register(new Setting<Integer>("Attacks", 1, 1, 10));
    public Setting<Integer> offset = this.register(new Setting<Integer>("Offset", 0, 0, 2));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));

    public CrystalPredict() {
        super("CrystalPredict", "Uses packets to break crystals faster by predicting entity ID", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onToggle() {
        this.resetFields();
        if (CrystalPredict.mc.world != null) {
            this.updateEntityID();
        }
    }

    @Override
    public void onUpdate() {
        if (this.render.getValue().booleanValue()) {
            for (Entity entity : CrystalPredict.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal)) continue;
                entity.setCustomNameTag(String.valueOf(entity.entityId));
                entity.setAlwaysRenderNameTag(true);
            }
        }
    }

    @Override
    public void onLogout() {
        this.resetFields();
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onSendPacket(PacketEvent.Send event) {
        if (this.strict.getValue().booleanValue()) {
            if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (CrystalPredict.mc.player.getHeldItem(packet.hand).getItem() instanceof ItemEndCrystal) {
                    if (this.checkPos.getValue().booleanValue() && !BlockUtil.canPlaceCrystal(packet.position, this.entitycheck.getValue(), this.oneDot15.getValue()) || this.checkPlayers()) {
                        return;
                    }
                    this.updateEntityID();
                    for (int i = 1 - this.offset.getValue(); i <= this.attacks.getValue(); ++i) {
                        this.attackID(packet.position, this.highestID + i);
                    }
                }
            }
            if (event.getStage() == 0 && this.rotating && this.rotate.getValue().booleanValue() && event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
                packet2.yaw = this.yaw;
                packet2.pitch = this.pitch;
                ++this.rotationPacketsSpoofed;
                if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                    this.rotating = false;
                    this.rotationPacketsSpoofed = 0;
                }
            }
        } else if (!this.strict.getValue().booleanValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if ((WorldUtils.getBlock(packet.position) == Blocks.OBSIDIAN || WorldUtils.getBlock(packet.position) == Blocks.BEDROCK) && CrystalPredict.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                int id = 0;
                for (Entity e : CrystalPredict.mc.world.loadedEntityList) {
                    if (e.entityId <= id) continue;
                    id = e.entityId;
                }
                this.attackByID(id + 1);
                this.attackByID(id + 2);
                this.attackByID(id + 3);
                this.attackByID(id + 4);
                this.attackByID(id + 5);
                OctoHack.LOGGER.info(id);
            }
        }
    }

    private void attackByID(int entityId) {
        CPacketUseEntity sequentialCrystal = new CPacketUseEntity();
        sequentialCrystal.entityId = entityId;
        sequentialCrystal.action = CPacketUseEntity.Action.ATTACK;
        CrystalPredict.mc.player.connection.sendPacket(sequentialCrystal);
    }

    private void attackID(BlockPos pos, int id) {
        Entity entity = CrystalPredict.mc.world.getEntityByID(id);
        if (entity == null || entity instanceof EntityEnderCrystal) {
            AttackThread attackThread = new AttackThread(id, pos, this.delay.getValue(), this);
            if (this.delay.getValue() == 0) {
                attackThread.run();
            } else {
                attackThread.start();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            this.checkID(((SPacketSpawnObject)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
            this.checkID(((SPacketSpawnExperienceOrb)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnPlayer) {
            this.checkID(((SPacketSpawnPlayer)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
            this.checkID(((SPacketSpawnGlobalEntity)event.getPacket()).getEntityId());
        } else if (event.getPacket() instanceof SPacketSpawnPainting) {
            this.checkID(((SPacketSpawnPainting)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnMob) {
            this.checkID(((SPacketSpawnMob)event.getPacket()).getEntityID());
        }
    }

    private void checkID(int id) {
        if (id > this.highestID) {
            this.highestID = id;
        }
    }

    public void updateEntityID() {
        for (Entity entity : CrystalPredict.mc.world.loadedEntityList) {
            if (entity.getEntityId() <= this.highestID) continue;
            this.highestID = entity.getEntityId();
        }
    }

    private boolean checkPlayers() {
        if (this.antiIllegal.getValue().booleanValue()) {
            for (EntityPlayer player : CrystalPredict.mc.world.playerEntities) {
                if (!this.checkItem(player.getHeldItemMainhand()) && !this.checkItem(player.getHeldItemOffhand())) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkItem(ItemStack stack) {
        return stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemExpBottle || stack.getItem() == Items.STRING;
    }

    public void rotateTo(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(CrystalPredict.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
    }

    private void resetFields() {
        this.rotating = false;
        this.highestID = -1000000;
    }

    public static class AttackThread
    extends Thread {
        private final BlockPos pos;
        private final int id;
        private final int delay;
        private final CrystalPredict crystalPredict;

        public AttackThread(int idIn, BlockPos posIn, int delayIn, CrystalPredict crystalPredictIn) {
            this.id = idIn;
            this.pos = posIn;
            this.delay = delayIn;
            this.crystalPredict = crystalPredictIn;
        }

        @Override
        public void run() {
            try {
                if (this.delay != 0) {
                    TimeUnit.MILLISECONDS.sleep(this.delay);
                }
                Util.mc.addScheduledTask(() -> {
                    if (!Feature.fullNullCheck()) {
                        CPacketUseEntity attack = new CPacketUseEntity();
                        attack.entityId = this.id;
                        attack.action = CPacketUseEntity.Action.ATTACK;
                        this.crystalPredict.rotateTo(this.pos.up());
                        Util.mc.player.connection.sendPacket(attack);
                        Util.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    }
                });
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
