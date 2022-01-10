package me.primooctopus33.octohack.client.modules.misc;

import com.mojang.authlib.GameProfile;
import java.util.Random;
import java.util.UUID;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.EnumStages;
import me.primooctopus33.octohack.event.events.EventTotemPop;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.manager.TotempopManager;
import me.primooctopus33.octohack.util.DamageUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FakePlayer
extends Module {
    public final Setting<Boolean> inv = this.register(new Setting<Boolean>("Copy Inventory", true));
    public final Setting<Boolean> pop = this.register(new Setting<Boolean>("Can Pop Totems", true));
    public final Setting<Boolean> move = this.register(new Setting<Boolean>("Can Move", true));
    private EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player for testing purposes", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        if (FakePlayer.nullCheck()) {
            return;
        }
        this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.world, new GameProfile(UUID.fromString("7a79368b-2235-4aba-a1cb-f88c06f03141"), "Primo"));
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
        if (this.inv.getValue().booleanValue()) {
            this.fakePlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
        FakePlayer.mc.world.addEntityToWorld(-100, this.fakePlayer);
    }

    @Override
    public void onTick() {
        if (this.fakePlayer != null) {
            Random random = new Random();
            this.fakePlayer.moveForward = FakePlayer.mc.player.moveForward + (float)random.nextInt(5) / 10.0f;
            this.fakePlayer.moveStrafing = FakePlayer.mc.player.moveStrafing + (float)random.nextInt(5) / 10.0f;
            if (this.move.getValue().booleanValue()) {
                this.travel(this.fakePlayer.moveStrafing, this.fakePlayer.moveVertical, this.fakePlayer.moveForward);
            }
        }
    }

    public void travel(float strafe, float vertical, float forward) {
        double d0 = this.fakePlayer.posY;
        float f1 = 0.8f;
        float f2 = 0.02f;
        float f3 = EnchantmentHelper.getDepthStriderModifier((EntityLivingBase)this.fakePlayer);
        if (f3 > 3.0f) {
            f3 = 3.0f;
        }
        if (!this.fakePlayer.onGround) {
            f3 *= 0.5f;
        }
        if (f3 > 0.0f) {
            f1 += (0.54600006f - f1) * f3 / 3.0f;
            f2 += (this.fakePlayer.getAIMoveSpeed() - f2) * f3 / 4.0f;
        }
        this.fakePlayer.moveRelative(strafe, vertical, forward, f2);
        this.fakePlayer.move(MoverType.SELF, this.fakePlayer.motionX, this.fakePlayer.motionY, this.fakePlayer.motionZ);
        this.fakePlayer.motionX *= (double)f1;
        this.fakePlayer.motionY *= (double)0.8f;
        this.fakePlayer.motionZ *= (double)f1;
        if (!this.fakePlayer.hasNoGravity()) {
            this.fakePlayer.motionY -= 0.02;
        }
        if (this.fakePlayer.collidedHorizontally && this.fakePlayer.isOffsetPositionInLiquid(this.fakePlayer.motionX, this.fakePlayer.motionY + (double)0.6f - this.fakePlayer.posY + d0, this.fakePlayer.motionZ)) {
            this.fakePlayer.motionY = 0.3f;
        }
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @Override
    public void onUpdate() {
        if (FakePlayer.nullCheck()) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (FakePlayer.nullCheck()) {
                return;
            }
            FakePlayer.mc.world.removeEntity(this.fakePlayer);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void doPop(Entity entity) {
        FakePlayer.mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, 30);
        FakePlayer.mc.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        block12: {
            if (this.pop.getValue().booleanValue()) {
                try {
                    if (!(event.getPacket() instanceof SPacketDestroyEntities)) break block12;
                    SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
                    for (int id : packet.getEntityIDs()) {
                        Entity entity = FakePlayer.mc.world.getEntityByID(id);
                        if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(this.fakePlayer) < 144.0)) continue;
                        float rawDamage = DamageUtil.calculateDamage(entity.getPositionVector(), (Entity)this.fakePlayer);
                        float absorption = this.fakePlayer.getAbsorptionAmount() - rawDamage;
                        boolean hasHealthDmg = absorption < 0.0f;
                        float health = this.fakePlayer.getHealth() + absorption;
                        if (hasHealthDmg && health > 0.0f) {
                            this.fakePlayer.setHealth(health);
                            this.fakePlayer.setAbsorptionAmount(0.0f);
                        } else if (health > 0.0f) {
                            this.fakePlayer.setAbsorptionAmount(absorption);
                        } else {
                            this.fakePlayer.setHealth(2.0f);
                            this.fakePlayer.setAbsorptionAmount(8.0f);
                            this.fakePlayer.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 5));
                            this.fakePlayer.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1));
                            try {
                                FakePlayer.mc.player.connection.handleEntityStatus(new SPacketEntityStatus(this.fakePlayer, 35));
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                            if (TotempopManager.totemMap.containsKey(this.fakePlayer)) {
                                int times = TotempopManager.totemMap.get(this.fakePlayer) + 1;
                                OctoHack.dispatcher.post(new EventTotemPop(EnumStages.PRE, this.fakePlayer, times));
                                TotempopManager.totemMap.remove(this.fakePlayer);
                                TotempopManager.totemMap.put(this.fakePlayer, times);
                            } else {
                                OctoHack.dispatcher.post(new EventTotemPop(EnumStages.PRE, this.fakePlayer, 1));
                                TotempopManager.totemMap.put(this.fakePlayer, 1);
                            }
                        }
                        this.fakePlayer.hurtTime = 5;
                    }
                }
                catch (Exception e) {
                    return;
                }
            }
        }
    }
}
