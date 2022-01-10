package me.primooctopus33.octohack.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.util.math.Vec3d;

public class AntiBowBomb
extends Module {
    public final Setting<Boolean> yCheck = this.register(new Setting<Boolean>("Y Check", false));
    public final Setting<Boolean> notWhileBow = this.register(new Setting<Boolean>("Not While Bow", true));

    public AntiBowBomb() {
        super("AntiBowBomb", "Attempts to block bowbomb with a shield", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        EntityPlayer bowBombers = this.getBowBombers();
        int oldSlot = AntiBowBomb.mc.player.inventory.currentItem;
        if (bowBombers == null || AntiBowBomb.nullCheck() || this.yCheck.getValue().booleanValue() && this.notOnVulnerableY(bowBombers)) {
            if (AntiBowBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemShield || AntiBowBomb.mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
                AntiBowBomb.mc.gameSettings.keyBindUseItem.pressed = false;
                AntiBowBomb.mc.player.inventory.currentItem = oldSlot;
            }
            return;
        }
        if (InventoryUtil.find(Items.SHIELD) == -1) {
            Command.sendMessage(ChatFormatting.WHITE + "<AntiBowBomb> " + ChatFormatting.GRAY + "You have no shield in your hotbar!");
            return;
        }
        int shieldSlot = InventoryUtil.find(Items.SHIELD);
        RotationUtil.faceVectorPacketInstant(new Vec3d(bowBombers.posX, bowBombers.posY, bowBombers.posZ));
        AntiBowBomb.mc.player.inventory.currentItem = shieldSlot;
        if (!(AntiBowBomb.mc.player.getHeldItemMainhand().getItem() instanceof ItemShield) && !(AntiBowBomb.mc.player.getHeldItemOffhand().getItem() instanceof ItemShield)) {
            return;
        }
        AntiBowBomb.mc.gameSettings.keyBindUseItem.pressed = true;
    }

    public EntityPlayer getBowBombers() {
        if (AntiBowBomb.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer potentialBowBomber = null;
        for (EntityPlayer target : AntiBowBomb.mc.world.playerEntities) {
            if (!(target.getHeldItemMainhand().getItem() instanceof ItemBow) && !(target.getHeldItemOffhand().getItem() instanceof ItemBow) || !target.isHandActive() || target == AntiBowBomb.mc.player) continue;
            potentialBowBomber = target;
        }
        return potentialBowBomber;
    }

    public boolean notOnVulnerableY(EntityPlayer entity) {
        return entity.posY == AntiBowBomb.mc.player.posY && entity.posY == AntiBowBomb.mc.player.posY - 1.0;
    }
}
