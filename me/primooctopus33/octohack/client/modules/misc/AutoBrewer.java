package me.primooctopus33.octohack.client.modules.misc;

import java.util.Comparator;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.PlayerUtil;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoBrewer
extends Module {
    public final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.STRENGTH));
    public final Setting<Boolean> grabMaterials = this.register(new Setting<Boolean>("Grab Materials", false));
    public final Setting<Integer> bRange = this.register(new Setting<Integer>("Brewing Stand Range", 5, 1, 6));
    public final Setting<Integer> cRange = this.register(new Setting<Integer>("Chest Range", 5, 1, 6));
    boolean doBrewing = false;
    boolean doneGrabbingMaterials = false;

    public AutoBrewer() {
        super("AutoBrewer", "Automatically brews potions", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        this.doneGrabbingMaterials = false;
    }

    @Override
    public void onDisable() {
        this.doneGrabbingMaterials = false;
    }

    @SubscribeEvent
    public void onPlayerUpdate(UpdateWalkingPlayerEvent event) {
        this.doBrewing = false;
        BlockPos closestBrewingStand = BlockUtil.getSphere(PlayerUtil.getPlayerPosFloored(), this.bRange.getValue().intValue(), this.bRange.getValue(), false, true, 0).stream().filter(pos -> this.isBrewingStand((BlockPos)pos)).min(Comparator.comparing(pos -> EntityUtil.getDistPlayerToBlock(AutoBrewer.mc.player, pos))).orElse(null);
        BlockPos closestChest = BlockUtil.getSphere(PlayerUtil.getPlayerPosFloored(), this.cRange.getValue().intValue(), this.cRange.getValue(), false, true, 0).stream().filter(pos -> this.isChest((BlockPos)pos)).min(Comparator.comparing(pos -> EntityUtil.getDistPlayerToBlock(AutoBrewer.mc.player, pos))).orElse(null);
        if (this.grabMaterials.getValue().booleanValue()) {
            if (closestChest != null && !this.doneGrabbingMaterials) {
                int i;
                double[] cPos = EntityUtil.calcLooking((double)closestChest.getX() + 0.5, (double)closestChest.getY() - 0.5, (double)closestChest.getZ() + 0.5, AutoBrewer.mc.player);
                if (!(AutoBrewer.mc.currentScreen instanceof GuiChest)) {
                    AutoBrewer.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(closestChest, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                }
                if (AutoBrewer.mc.currentScreen instanceof GuiChest) {
                    for (i = 0; i < ((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.getInventory().size() - 36; ++i) {
                        if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.GLASS_BOTTLE) continue;
                        AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                    }
                    switch (this.mode.getValue()) {
                        case STRENGTH: {
                            for (i = 0; i < ((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.getInventory().size() - 36; ++i) {
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.BLAZE_POWDER) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.GLOWSTONE_DUST) continue;
                                AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                            }
                            this.doneGrabbingMaterials = true;
                            this.doBrewing = true;
                        }
                        case SPEED: {
                            for (i = 0; i < ((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.getInventory().size() - 36; ++i) {
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.SUGAR) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.REDSTONE) continue;
                                AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                            }
                            this.doneGrabbingMaterials = true;
                            this.doBrewing = true;
                        }
                        case SLOWNESS: {
                            for (i = 0; i < ((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.getInventory().size() - 36; ++i) {
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.SUGAR) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.FERMENTED_SPIDER_EYE) {
                                    AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.REDSTONE) continue;
                                AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                            }
                            this.doneGrabbingMaterials = true;
                            this.doBrewing = true;
                        }
                        case WEAKNESS: {
                            for (i = 0; i < ((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.getInventory().size() - 36; ++i) {
                                if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.FERMENTED_SPIDER_EYE) continue;
                                AutoBrewer.mc.playerController.windowClick(((GuiChest)((Object)AutoBrewer.mc.currentScreen)).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                            }
                            this.doneGrabbingMaterials = true;
                            this.doBrewing = true;
                        }
                    }
                }
                if (closestBrewingStand != null) {
                    if (!(AutoBrewer.mc.currentScreen instanceof GuiBrewingStand) && this.doBrewing) {
                        AutoBrewer.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(closestBrewingStand, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                    } else {
                        switch (this.mode.getValue()) {
                            case STRENGTH: {
                                for (i = 0; i < 45; ++i) {
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.BLAZE_POWDER) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.GLOWSTONE_DUST) continue;
                                    AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                            }
                            case SPEED: {
                                for (i = 0; i < 45; ++i) {
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.SUGAR) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.REDSTONE) continue;
                                    AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                            }
                            case SLOWNESS: {
                                for (i = 0; i < 45; ++i) {
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.NETHER_WART) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.SUGAR) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() == Items.FERMENTED_SPIDER_EYE) {
                                        AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                    }
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.REDSTONE) continue;
                                    AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                            }
                            case WEAKNESS: {
                                for (i = 0; i < 45; ++i) {
                                    if (AutoBrewer.mc.player.openContainer.getSlot(i).getStack().getItem() != Items.FERMENTED_SPIDER_EYE) continue;
                                    AutoBrewer.mc.playerController.windowClick(((GuiInventory)AutoBrewer.mc.currentScreen).field_147002_h.windowId, i, 0, ClickType.QUICK_MOVE, AutoBrewer.mc.player);
                                }
                                break;
                            }
                        }
                    }
                } else {
                    Command.sendMessage("Cannot find a Chest and or Brewing Stand within range... Toggling!");
                    this.disable();
                }
            }
            return;
        }
    }

    private boolean isBrewingStand(BlockPos pos) {
        IBlockState blockState = AutoBrewer.mc.world.getBlockState(pos);
        return blockState.getBlock() instanceof BlockBrewingStand;
    }

    private boolean isChest(BlockPos pos) {
        IBlockState blockState = AutoBrewer.mc.world.getBlockState(pos);
        return blockState.getBlock() instanceof BlockChest;
    }

    public static enum Mode {
        STRENGTH,
        SPEED,
        WEAKNESS,
        SLOWNESS;

    }
}
