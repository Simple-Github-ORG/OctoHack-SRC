package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.event.events.Render2DEvent;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShulkerTips
extends Module {
    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static ShulkerTips INSTANCE = new ShulkerTips();
    public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<EntityPlayer, ItemStack>();
    public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<EntityPlayer, Timer>();
    private int textRadarY = 0;

    public ShulkerTips() {
        super("ShulkerTips", "Shows the contents of a shulker someone is holding", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static ShulkerTips getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShulkerTips();
        }
        return INSTANCE;
    }

    public static void displayInv(ItemStack stack, String name) {
        try {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox)((Object)item);
            entityBox.field_145854_h = shulker.getBlock();
            entityBox.setWorldObj(ShulkerTips.mc.world);
            ItemStackHelper.loadAllItems((NBTTagCompound)stack.getTagCompound().getCompoundTag("BlockEntityTag"), (NonNullList)entityBox.items);
            entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.func_190575_a(name == null ? stack.getDisplayName() : name);
            new Thread(() -> {
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ShulkerTips.mc.player.displayGUIChest(entityBox);
            }).start();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (ShulkerTips.fullNullCheck()) {
            return;
        }
        for (EntityPlayer player : ShulkerTips.mc.world.playerEntities) {
            if (player == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) || ShulkerTips.mc.player == player) continue;
            ItemStack stack = player.getHeldItemMainhand();
            this.spiedPlayers.put(player, stack);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (ShulkerTips.fullNullCheck()) {
            return;
        }
        int x = -3;
        int y = 124;
        this.textRadarY = 0;
        for (EntityPlayer player : ShulkerTips.mc.world.playerEntities) {
            Timer playerTimer;
            if (this.spiedPlayers.get(player) == null) continue;
            if (player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox)) {
                playerTimer = this.playerTimers.get(player);
                if (playerTimer == null) {
                    Timer timer = new Timer();
                    timer.reset();
                    this.playerTimers.put(player, timer);
                } else if (playerTimer.passedS(3.0)) {
                    continue;
                }
            } else if (player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox && (playerTimer = this.playerTimers.get(player)) != null) {
                playerTimer.reset();
                this.playerTimers.put(player, playerTimer);
            }
            ItemStack stack = this.spiedPlayers.get(player);
            this.renderShulkerToolTip(stack, x, y, player.getName());
            this.textRadarY = (y += 78) - 10 - 114 + 2;
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void makeTooltip(ItemTooltipEvent event) {
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 57, 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.disableDepth();
            Color color = new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), 200);
            this.renderer.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            NonNullList nonnulllist = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
            ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack)nonnulllist.get(i);
                ShulkerTips.mc.getItemRenderer().itemRenderer.zLevel = 501.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(ShulkerTips.mc.fontRenderer, itemStack, iX, iY, null);
                ShulkerTips.mc.getItemRenderer().itemRenderer.zLevel = 0.0f;
            }
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }
}
