package me.primooctopus33.octohack.client.modules.render;

import com.mojang.authlib.GameProfile;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.Util;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeF5
extends Module {
    public static Setting<Integer> range;
    private static float cameraYaw;
    private static float cameraPitch;
    private static EntityPlayerCamera camera;
    private int flag = -1;

    public FreeF5() {
        super("FreeF5", "Allows you to get a better view of yourself and your surroundings", Module.Category.RENDER, true, false, false);
        range = this.register(new Setting<Integer>("Range", 3, 0, 7));
    }

    private void updateCamera() {
        if (FreeF5.nullCheck() || camera == null) {
            return;
        }
        if (FreeF5.mc.inGameHasFocus) {
            float f = FreeF5.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f1 = f * f * f * 8.0f;
            double dx = (double)((float)Mouse.getDX() * f1) * 0.15;
            double dy = (double)((float)Mouse.getDY() * f1) * 0.15;
            cameraYaw = (float)((double)cameraYaw + dx);
            cameraPitch = (float)((double)cameraPitch + dy * -1.0);
            cameraPitch = MathHelper.clamp((float)cameraPitch, (float)-90.0f, (float)90.0f);
            cameraYaw = MathHelper.clamp((float)cameraYaw, (float)(cameraYaw + -100.0f), (float)(cameraYaw + 100.0f));
            FreeF5.camera.rotationPitch = cameraPitch;
            FreeF5.camera.rotationYaw = cameraYaw;
        }
    }

    private void updateCamera2() {
        double x = FreeF5.mc.player.posX + (double)range.getValue().intValue() * Math.cos(Math.toRadians(cameraPitch));
        double z = FreeF5.mc.player.posY + (double)range.getValue().intValue() * Math.sin(Math.toRadians(cameraPitch));
        double dist = Math.abs(FreeF5.mc.player.posX - x);
        double x1 = FreeF5.mc.player.posX + dist * Math.cos(Math.toRadians(cameraYaw - 90.0f));
        double z1 = FreeF5.mc.player.posZ + dist * Math.sin(Math.toRadians(cameraYaw - 90.0f));
        this.setPosition(camera, x1, z, z1);
        FreeF5.camera.inventory.copyInventory(FreeF5.mc.player.inventory);
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        this.setEnabled(false);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (FreeF5.nullCheck() || camera == null) {
            return;
        }
        this.updateCamera();
        this.updateCamera2();
    }

    @SubscribeEvent
    public void onRender(RenderHandEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        EntityPlayerSP player;
        if (FreeF5.nullCheck()) {
            return;
        }
        cameraPitch = FreeF5.mc.player.rotationPitch;
        cameraYaw = FreeF5.mc.player.rotationYaw;
        camera = new EntityPlayerCamera(FreeF5.mc.player.getGameProfile());
        if (FreeF5.mc.gameSettings.thirdPersonView != 0) {
            this.flag = FreeF5.mc.gameSettings.thirdPersonView;
        }
        if ((player = FreeF5.mc.player) != null) {
            camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            camera.setRotationYawHead(player.rotationYaw);
        }
        FreeF5.mc.world.addEntityToWorld(-9283, camera);
        FreeF5.mc.renderViewEntity = camera;
    }

    @Override
    public void onUpdate() {
        if (FreeF5.nullCheck()) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        if (FreeF5.nullCheck() || camera == null) {
            return;
        }
        FreeF5.mc.renderViewEntity = FreeF5.mc.player;
        FreeF5.mc.world.removeEntity(camera);
        if (this.flag != -1) {
            FreeF5.mc.gameSettings.thirdPersonView = this.flag;
            this.flag = -1;
        }
    }

    private void setPosition(Entity en, double x, double y, double z) {
        this.setPosition(en, x, y, z, en.rotationYaw, en.rotationPitch);
    }

    private void setPosition(Entity en, double x, double y, double z, float yaw, float cameraPitch) {
        en.prevPosX = en.posX = x;
        en.prevPosY = en.posY = y;
        en.prevPosZ = en.posZ = z;
        en.rotationYaw = yaw;
        en.rotationPitch = cameraPitch;
    }

    private static class EntityPlayerCamera
    extends EntityOtherPlayerMP {
        public EntityPlayerCamera(GameProfile gameProfileIn) {
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
}
