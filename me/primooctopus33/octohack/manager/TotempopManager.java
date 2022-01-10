package me.primooctopus33.octohack.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.event.EnumStages;
import me.primooctopus33.octohack.event.events.EventPacketRecieve;
import me.primooctopus33.octohack.event.events.EventTotemPop;
import me.primooctopus33.octohack.util.Util;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TotempopManager
implements Util,
Listenable {
    public static ConcurrentHashMap<EntityLivingBase, Integer> totemMap;
    @EventHandler
    private final Listener<EventPacketRecieve> packetRecieveListener = new Listener<EventPacketRecieve>(event -> {
        if (TotempopManager.mc.player == null || TotempopManager.mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)((Object)event.getPacket());
            EntityLivingBase entity = (EntityLivingBase)packet.getEntity(TotempopManager.mc.world);
            if (packet.getOpCode() == 35) {
                if (totemMap.containsKey(entity)) {
                    int times = totemMap.get(entity) + 1;
                    OctoHack.dispatcher.post(new EventTotemPop(EnumStages.PRE, entity, times));
                    totemMap.remove(entity);
                    totemMap.put(entity, times);
                } else {
                    OctoHack.dispatcher.post(new EventTotemPop(EnumStages.PRE, entity, 1));
                    totemMap.put(entity, 1);
                }
            }
        }
    }, new Predicate[0]);

    public TotempopManager() {
        totemMap = new ConcurrentHashMap();
        OctoHack.dispatcher.subscribe((Object)this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void update() {
        for (EntityLivingBase entity : totemMap.keySet()) {
            if (TotempopManager.mc.world.loadedEntityList.contains(entity)) continue;
            totemMap.remove(entity);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (TotempopManager.mc.player == null || TotempopManager.mc.world == null) {
            totemMap.clear();
            return;
        }
        TotempopManager.update();
    }

    public static int getPops(EntityLivingBase entity) {
        if (totemMap.containsKey(entity)) {
            return totemMap.get(entity);
        }
        return 0;
    }

    public static int getPops(String name) {
        boolean flag = false;
        EntityLivingBase e = null;
        for (Entity entity : TotempopManager.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || !entity.getName().equals(name)) continue;
            flag = true;
            e = (EntityLivingBase)entity;
            break;
        }
        if (flag && totemMap.containsKey(e)) {
            return totemMap.get(e);
        }
        return 0;
    }
}
