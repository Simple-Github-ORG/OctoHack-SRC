package me.primooctopus33.octohack.client.modules.combat;

import java.util.concurrent.TimeUnit;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.modules.combat.CrystalPredict;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class CrystalPredict$AttackThread
extends Thread {
    private final BlockPos pos;
    private final int id;
    private final int delay;
    private final CrystalPredict crystalPredict;

    public CrystalPredict$AttackThread(int idIn, BlockPos posIn, int delayIn, CrystalPredict crystalPredictIn) {
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
