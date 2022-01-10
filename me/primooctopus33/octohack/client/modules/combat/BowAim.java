package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BowAim
extends Module {
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("PacketRotate", true));

    public BowAim() {
        super("BowAim", "Automatically aims your bow at your opponent", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (BowAim.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && BowAim.mc.player.isHandActive() && BowAim.mc.player.getItemInUseMaxCount() >= 3) {
            EntityPlayer player = null;
            float tickDis = 100.0f;
            for (EntityPlayer p : BowAim.mc.world.playerEntities) {
                float dis;
                if (p instanceof EntityPlayerSP || OctoHack.friendManager.isFriend(p.getName()) || !((dis = p.getDistance(BowAim.mc.player)) < tickDis)) continue;
                tickDis = dis;
                player = p;
            }
            if (player != null) {
                Vec3d pos = BowAim.interpolateEntity(player, mc.getRenderPartialTicks());
                float[] angels = BowAim.calcAngle(BowAim.interpolateEntity(BowAim.mc.player, mc.getRenderPartialTicks()), pos);
                if (this.packet.getValue().booleanValue()) {
                    BowAim.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angels[0], angels[1], BowAim.mc.player.onGround));
                } else {
                    BowAim.mc.player.rotationYaw = angels[0];
                    BowAim.mc.player.rotationPitch = angels[1];
                }
            }
        }
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((double)(difX * difX + difZ * difZ));
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)))};
    }
}
