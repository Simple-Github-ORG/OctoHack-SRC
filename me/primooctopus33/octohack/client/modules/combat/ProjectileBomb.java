package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSnowball;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProjectileBomb
extends Module {
    public final Setting<Integer> spoofs = this.register(new Setting<Integer>("Spoofs", 100, 0, 500));
    public final Setting<Boolean> bowOnly = this.register(new Setting<Boolean>("Bow Only", true));

    public ProjectileBomb() {
        super("ProjectileBomb", "Allows your bow to deal more damage by spoofing arrow momentum using packets", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        block3: {
            block4: {
                if (!(event.getPacket() instanceof CPacketPlayerDigging) || !((CPacketPlayerDigging)event.getPacket()).getAction().equals(CPacketPlayerDigging.Action.RELEASE_USE_ITEM)) break block3;
                if (!this.bowOnly.getValue().booleanValue()) break block4;
                if (!(ProjectileBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) break block3;
                ProjectileBomb.mc.player.connection.sendPacket(new CPacketEntityAction(ProjectileBomb.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                for (int ticks = 0; ticks < this.spoofs.getValue(); ++ticks) {
                    ProjectileBomb.mc.player.connection.sendPacket(new CPacketPlayer.Position(ProjectileBomb.mc.player.posX, ProjectileBomb.mc.player.posY + 1.0E-10, ProjectileBomb.mc.player.posZ, false));
                    ProjectileBomb.mc.player.connection.sendPacket(new CPacketPlayer.Position(ProjectileBomb.mc.player.posX, ProjectileBomb.mc.player.posY - 1.0E-10, ProjectileBomb.mc.player.posZ, true));
                }
                break block3;
            }
            if (event.getPacket() instanceof CPacketPlayerTryUseItem && (ProjectileBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || ProjectileBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemEnderPearl || ProjectileBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemSnowball || ProjectileBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemEgg)) {
                ProjectileBomb.mc.player.connection.sendPacket(new CPacketEntityAction(ProjectileBomb.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                for (int ticks = 0; ticks < this.spoofs.getValue(); ++ticks) {
                    ProjectileBomb.mc.player.connection.sendPacket(new CPacketPlayer.Position(ProjectileBomb.mc.player.posX, ProjectileBomb.mc.player.posY + 1.0E-10, ProjectileBomb.mc.player.posZ, false));
                    ProjectileBomb.mc.player.connection.sendPacket(new CPacketPlayer.Position(ProjectileBomb.mc.player.posX, ProjectileBomb.mc.player.posY - 1.0E-10, ProjectileBomb.mc.player.posZ, true));
                }
            }
        }
    }
}
