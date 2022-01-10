package me.primooctopus33.octohack.client.modules.client;

import java.awt.Color;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TargetHud
extends Module {
    public final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(500.0f), Float.valueOf(0.1f), Float.valueOf(700.0f)));
    public final Setting<Integer> x = this.register(new Setting<Integer>("X", 572, 0, 1000));
    public final Setting<Integer> y = this.register(new Setting<Integer>("Y", 437, 0, 600));
    public final Setting<Integer> r = this.register(new Setting<Integer>("Red", 255, 0, 255));
    public final Setting<Integer> g = this.register(new Setting<Integer>("Green", 255, 0, 255));
    public final Setting<Integer> b = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public final Setting<Integer> a = this.register(new Setting<Integer>("Alpha", 60, 0, 255));

    public TargetHud() {
        super("TargetHud", "Shows useful information about your target", Module.Category.CLIENT, true, false, false);
    }

    @Override
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        int currentColor = ColorUtil.toARGB(this.r.getValue(), this.g.getValue(), this.b.getValue(), this.a.getValue());
        int healthBarOutlineColor = ColorUtil.toARGB(this.r.getValue(), this.g.getValue(), this.b.getValue(), 200);
        EntityPlayer target = (EntityPlayer)EntityUtil.getTarget(true, false, true, false, false, this.range.getValue().floatValue(), EntityUtil.toMode("Closest"));
        Gui.drawRect((int)this.x.getValue(), (int)this.y.getValue(), (int)(this.x.getValue() + 245), (int)(this.y.getValue() + 130), (int)currentColor);
        if (target != null) {
            float health = target.getHealth() + target.getAbsorptionAmount();
            int ping = EntityUtil.getEntityPing(target);
            int unSafeColor = ColorUtil.toARGB(255, 40, 20, 250);
            int trueColor = ColorUtil.toARGB(0, 255, 120, 250);
            int falseColor = ColorUtil.toARGB(255, 40, 90, 250);
            Color pingColor = null;
            pingColor = ping < 49 ? new Color(0, 255, 20, 250) : (ping < 89 ? new Color(255, 250, 0, 250) : new Color(255, 40, 20, 250));
            Color distanceColor = null;
            distanceColor = target.getDistance(TargetHud.mc.player) < 20.0f ? new Color(255, 40, 20, 250) : (target.getDistance(TargetHud.mc.player) < 50.0f ? new Color(255, 250, 0, 250) : new Color(255, 40, 20, 250));
            Color healthColor = null;
            healthColor = health < 11.0f ? new Color(255, 40, 90, 250) : (health < 21.0f ? new Color(255, 240, 0, 250) : new Color(0, 255, 120, 250));
            Color statusColor = null;
            if (target.inventory.armorItemInSlot(2).getItem().equals(Items.ELYTRA)) {
                statusColor = new Color(255, 250, 0, 250);
            } else if (OctoHack.friendManager.isFriend(target)) {
                statusColor = ClickGui.getInstance().getCurrentColor();
            } else if (target.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE) && target.inventory.armorItemInSlot(1).getItem().equals(Items.DIAMOND_LEGGINGS) && target.inventory.armorItemInSlot(3).getItem().equals(Items.DIAMOND_HELMET) && target.inventory.armorItemInSlot(0).getItem().equals(Items.DIAMOND_BOOTS) && !OctoHack.friendManager.isFriend(target)) {
                statusColor = new Color(255, 40, 20, 250);
            } else if (!(target.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE) && target.inventory.armorItemInSlot(1).getItem().equals(Items.DIAMOND_LEGGINGS) && target.inventory.armorItemInSlot(3).getItem().equals(Items.DIAMOND_HELMET) && target.inventory.armorItemInSlot(0).getItem().equals(Items.DIAMOND_BOOTS) || OctoHack.friendManager.isFriend(target))) {
                statusColor = new Color(0, 255, 20, 250);
            }
            String status = "";
            if (target.inventory.armorItemInSlot(2).getItem().equals(Items.ELYTRA)) {
                status = "Wasp";
            } else if (OctoHack.friendManager.isFriend(target)) {
                status = "Friend";
            } else if (target.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE) && target.inventory.armorItemInSlot(1).getItem().equals(Items.DIAMOND_LEGGINGS) && target.inventory.armorItemInSlot(3).getItem().equals(Items.DIAMOND_HELMET) && target.inventory.armorItemInSlot(0).getItem().equals(Items.DIAMOND_BOOTS) && !OctoHack.friendManager.isFriend(target)) {
                status = "Threat";
            } else if (!(target.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE) && target.inventory.armorItemInSlot(1).getItem().equals(Items.DIAMOND_LEGGINGS) && target.inventory.armorItemInSlot(3).getItem().equals(Items.DIAMOND_HELMET) && target.inventory.armorItemInSlot(0).getItem().equals(Items.DIAMOND_BOOTS) || OctoHack.friendManager.isFriend(target))) {
                status = "Harmless";
            }
            String safety = "";
            if (this.isUnsafe(target)) {
                safety = "Unsafe";
            } else if (!this.isUnsafe(target)) {
                safety = "Safe";
            }
            String inBlock = "";
            if (this.isInBlock(target)) {
                inBlock = "True";
            } else if (!this.isInBlock(target)) {
                inBlock = "False";
            }
            String isMoving = "";
            if (TargetHud.isEntityMoving(target)) {
                isMoving = "True";
            } else if (!TargetHud.isEntityMoving(target)) {
                isMoving = "False";
            }
            OctoHack.textManager.drawStringWithShadow(target.getName(), this.x.getValue() + 10, this.y.getValue() + 10, OctoHack.friendManager.isFriend(target) ? ClickGui.getInstance().getCurrentColorHex() : -1);
            OctoHack.textManager.drawStringWithShadow("Safety : " + safety, this.x.getValue() + 10, this.y.getValue() + 20, this.isUnsafe(target) ? unSafeColor : -1);
            OctoHack.textManager.drawStringWithShadow(ping + " MS", this.x.getValue() + 10, this.y.getValue() + 30, pingColor.getRGB());
            OctoHack.textManager.drawStringWithShadow(health + " Health", this.x.getValue() + 10, this.y.getValue() + 40, healthColor.getRGB());
            OctoHack.textManager.drawStringWithShadow("Status : " + status, this.x.getValue() + 10, this.y.getValue() + 50, statusColor.getRGB());
            OctoHack.textManager.drawStringWithShadow("Distance : " + (int)target.getDistance(TargetHud.mc.player), this.x.getValue() + 10, this.y.getValue() + 90, distanceColor.getRGB());
            OctoHack.textManager.drawStringWithShadow("In Block : " + inBlock, this.x.getValue() + 10, this.y.getValue() + 60, this.isInBlock(target) ? trueColor : falseColor);
            OctoHack.textManager.drawStringWithShadow("Is Moving : " + isMoving, this.x.getValue() + 10, this.y.getValue() + 70, TargetHud.isEntityMoving(target) ? trueColor : falseColor);
            OctoHack.textManager.drawStringWithShadow("Target Speed : " + (int)TargetHud.getEntitySpeed(target), this.x.getValue() + 10, this.y.getValue() + 80, TargetHud.isEntityMoving(target) ? trueColor : falseColor);
            try {
                GuiInventory.drawEntityOnScreen((int)(this.x.getValue() + 120), (int)(this.y.getValue() + 90), (int)40, (float)0.0f, (float)0.0f, (EntityLivingBase)target);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            RenderUtil.drawOutlineRect(this.x.getValue() + 10, this.y.getValue() + 100, this.x.getValue() + 235, this.y.getValue() + 120, healthBarOutlineColor);
            Gui.drawRect((int)(this.x.getValue() + 13), (int)(this.y.getValue() + 105), (int)(this.x.getValue() + 15 + (int)health * 6), (int)(this.y.getValue() + 115), (int)healthColor.getRGB());
            int iteration = 0;
            int i = this.x.getValue() + 140;
            int y = this.y.getValue() + this.renderer.getFontHeight() * 4 - 25;
            for (ItemStack is : target.inventory.armorInventory) {
                ++iteration;
                if (is.isEmpty()) continue;
                int x = i - 90 + (9 - iteration) * 20 + 2;
                GlStateManager.enableDepth();
                RenderUtil.itemRender.zLevel = 200.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(TargetHud.mc.fontRenderer, is, x, y, "");
                RenderUtil.itemRender.zLevel = 0.0f;
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                String s = is.getCount() > 1 ? is.getCount() + "" : "";
                this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
                int dmg = 0;
                int itemDurability = is.getMaxDamage() - is.getItemDamage();
                float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
                float red = 1.0f - green;
                dmg = 100 - (int)(red * 100.0f);
                this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8) - (float)this.renderer.getStringWidth(dmg + "") / 2.0f, y - 5, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        } else {
            OctoHack.textManager.drawStringWithShadow("No Target", this.x.getValue() + 10, this.y.getValue() + 10, -1);
        }
    }

    public boolean isInBlock(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        return TargetHud.mc.world.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    public static boolean isEntityMoving(Entity entity) {
        return TargetHud.getEntitySpeed(entity) != 0.0;
    }

    public static double getEntitySpeed(Entity entity) {
        if (entity != null) {
            double distTraveledLastTickX = entity.posX - entity.prevPosX;
            double distTraveledLastTickZ = entity.posZ - entity.prevPosZ;
            double speed = MathHelper.sqrt((double)(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ));
            return speed * 20.0;
        }
        return 0.0;
    }

    public boolean isUnsafe(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY - 1.0, player.posZ);
        if (BlockUtil.canPlaceCrystal(pos.south()) || BlockUtil.canPlaceCrystal(pos.south().south()) && TargetHud.mc.world.getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR) {
            return true;
        }
        if (BlockUtil.canPlaceCrystal(pos.east()) || BlockUtil.canPlaceCrystal(pos.east().east()) && TargetHud.mc.world.getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR) {
            return true;
        }
        if (BlockUtil.canPlaceCrystal(pos.west()) || BlockUtil.canPlaceCrystal(pos.west().west()) && TargetHud.mc.world.getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR) {
            return true;
        }
        return BlockUtil.canPlaceCrystal(pos.north()) || BlockUtil.canPlaceCrystal(pos.north().north()) && TargetHud.mc.world.getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR;
    }
}
