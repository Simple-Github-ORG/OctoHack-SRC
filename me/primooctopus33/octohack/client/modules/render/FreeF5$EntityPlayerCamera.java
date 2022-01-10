package me.primooctopus33.octohack.client.modules.render;

import com.mojang.authlib.GameProfile;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

class FreeF5$EntityPlayerCamera
extends EntityOtherPlayerMP {
    public FreeF5$EntityPlayerCamera(GameProfile gameProfileIn) {
        super(Util.mc.world, gameProfileIn);
    }

    public boolean isInvisible() {
        return true;
    }

    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }

    public boolean isSpectator() {
        return false;
    }
}
