package me.primooctopus33.octohack.client.modules.movement;

import java.util.Arrays;
import java.util.List;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.Feature;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.movement.Sprint;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Legit));
    private final Setting<Boolean> swing;
    private final Setting<Boolean> bSwitch;
    private final Setting<Boolean> center;
    private final Setting<Boolean> keepY;
    private final Setting<Boolean> sprint;
    private final Setting<Boolean> replenishBlocks;
    private final Setting<Boolean> down;
    private final Setting<Float> expand;
    private final List<Block> invalid;
    private final Timer timerMotion;
    private final Timer itemTimer;
    private final Timer timer;
    public Setting<Boolean> rotation = this.register(new Setting<Object>("Rotate", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.Fast));
    private int lastY;
    private BlockPos pos;
    private boolean teleported;

    public Scaffold() {
        super("Scaffold", "Places Blocks underneath you allowing you to move quickly through the air", Module.Category.MOVEMENT, true, false, false);
        this.swing = this.register(new Setting<Object>("Swing", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Legit));
        this.bSwitch = this.register(new Setting<Object>("Switch", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Legit));
        this.center = this.register(new Setting<Object>("Center", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.Legit));
        this.keepY = this.register(new Setting<Object>("Keep Y Level", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.Legit));
        this.sprint = this.register(new Setting<Object>("Auto Sprint", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Legit));
        this.replenishBlocks = this.register(new Setting<Object>("Refill Blocks In Hotbar", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Legit));
        this.down = this.register(new Setting<Object>("Build Down", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.Legit));
        this.expand = this.register(new Setting<Object>("Extend", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.mode.getValue() == Mode.Legit));
        this.invalid = Arrays.asList(Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.FLOWING_LAVA, Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.ANVIL, Blocks.CACTUS, Blocks.LADDER, Blocks.ENDER_CHEST);
        this.timerMotion = new Timer();
        this.itemTimer = new Timer();
        this.timer = new Timer();
    }

    public static void swap(int slot, int hotbarNum) {
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, Scaffold.mc.player);
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, hotbarNum, 0, ClickType.PICKUP, Scaffold.mc.player);
        Scaffold.mc.playerController.windowClick(Scaffold.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, Scaffold.mc.player);
        Scaffold.mc.playerController.updateController();
    }

    public static int getItemSlot(Container container, Item item) {
        int slot = 0;
        for (int i = 9; i < 45; ++i) {
            ItemStack is;
            if (!container.getSlot(i).getHasStack() || (is = container.getSlot(i).getStack()).getItem() != item) continue;
            slot = i;
        }
        return slot;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f;
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() == Mode.Fast) {
            BlockPos playerBlock;
            if (this.isOff() || Feature.fullNullCheck() || event.getStage() == 0) {
                return;
            }
            if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.timer.reset();
            }
            if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).add(0, -1, 0))) {
                if (BlockUtil.isValidBlock(playerBlock.add(0, -2, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.UP);
                } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, -1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
                    }
                    this.place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.WEST);
                    }
                    this.place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.SOUTH);
                    }
                    this.place(playerBlock.add(1, -1, 1), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.EAST);
                    }
                    this.place(playerBlock.add(1, -1, 1), EnumFacing.NORTH);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.Legit) {
            if (OctoHack.moduleManager.isModuleEnabled("Sprint") && (this.down.getValue().booleanValue() && Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() || !this.sprint.getValue().booleanValue())) {
                Scaffold.mc.player.setSprinting(false);
                Sprint.getInstance().disable();
            }
            if (this.replenishBlocks.getValue().booleanValue() && !(Scaffold.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && this.getBlockCountHotbar() <= 0) {
                for (int i = 9; i < 45; ++i) {
                    ItemStack is;
                    if (!Scaffold.mc.player.inventoryContainer.getSlot(i).getHasStack() || !((is = Scaffold.mc.player.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || this.invalid.contains(Block.getBlockFromItem((Item)is.getItem())) || i >= 36) continue;
                    Scaffold.swap(Scaffold.getItemSlot(Scaffold.mc.player.inventoryContainer, is.getItem()), 44);
                }
            }
            if (this.keepY.getValue().booleanValue()) {
                if (!Scaffold.isMoving(Scaffold.mc.player) && Scaffold.mc.gameSettings.keyBindJump.isKeyDown() || Scaffold.mc.player.collidedVertically || Scaffold.mc.player.onGround) {
                    this.lastY = MathHelper.floor((double)Scaffold.mc.player.posY);
                }
            } else {
                this.lastY = MathHelper.floor((double)Scaffold.mc.player.posY);
            }
            BlockData blockData = null;
            double x = Scaffold.mc.player.posX;
            double z = Scaffold.mc.player.posZ;
            double y = this.keepY.getValue() != false ? (double)this.lastY : Scaffold.mc.player.posY;
            double forward = Scaffold.mc.player.movementInput.moveForward;
            double strafe = Scaffold.mc.player.movementInput.moveStrafe;
            float yaw = Scaffold.mc.player.rotationYaw;
            if (!Scaffold.mc.player.collidedHorizontally) {
                double[] coords = this.getExpandCoords(x, z, forward, strafe, yaw);
                x = coords[0];
                z = coords[1];
            }
            if (this.canPlace(Scaffold.mc.world.getBlockState(new BlockPos(Scaffold.mc.player.posX, Scaffold.mc.player.posY - (double)(Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue() != false ? 2 : 1), Scaffold.mc.player.posZ)).getBlock())) {
                x = Scaffold.mc.player.posX;
                z = Scaffold.mc.player.posZ;
            }
            BlockPos blockBelow = new BlockPos(x, y - 1.0, z);
            if (Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue().booleanValue()) {
                blockBelow = new BlockPos(x, y - 2.0, z);
            }
            this.pos = blockBelow;
            if (Scaffold.mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
                blockData = this.getBlockData2(blockBelow);
            }
            if (blockData != null) {
                if (this.getBlockCountHotbar() <= 0 || !this.bSwitch.getValue().booleanValue() && !(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
                    return;
                }
                int heldItem = Scaffold.mc.player.inventory.currentItem;
                if (this.bSwitch.getValue().booleanValue()) {
                    for (int j = 0; j < 9; ++j) {
                        Scaffold.mc.player.inventory.getStackInSlot(j);
                        if (Scaffold.mc.player.inventory.getStackInSlot(j).getCount() == 0 || !(Scaffold.mc.player.inventory.getStackInSlot(j).getItem() instanceof ItemBlock) || this.invalid.contains(((ItemBlock)((Object)Scaffold.mc.player.inventory.getStackInSlot(j).getItem())).getBlock())) continue;
                        Scaffold.mc.player.inventory.currentItem = j;
                        break;
                    }
                }
                if (this.mode.getValue() == Mode.Legit) {
                    if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown() && Scaffold.mc.player.moveForward == 0.0f && Scaffold.mc.player.moveStrafing == 0.0f && !Scaffold.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        if (!this.teleported && this.center.getValue().booleanValue()) {
                            this.teleported = true;
                            BlockPos pos = new BlockPos(Scaffold.mc.player.posX, Scaffold.mc.player.posY, Scaffold.mc.player.posZ);
                            Scaffold.mc.player.setPosition((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
                        }
                        if (this.center.getValue().booleanValue() && !this.teleported) {
                            return;
                        }
                        Scaffold.mc.player.motionY = 0.42f;
                        Scaffold.mc.player.motionZ = 0.0;
                        Scaffold.mc.player.motionX = 0.0;
                        Scaffold.mc.player.motionY = -0.28;
                    } else {
                        this.timerMotion.reset();
                        if (this.teleported && this.center.getValue().booleanValue()) {
                            this.teleported = false;
                        }
                    }
                }
                if (Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, blockData.position, blockData.face, new Vec3d((double)blockData.position.getX() + Math.random(), (double)blockData.position.getY() + Math.random(), (double)blockData.position.getZ() + Math.random()), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                    if (this.swing.getValue().booleanValue()) {
                        Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
                    } else {
                        Scaffold.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    }
                }
                Scaffold.mc.player.inventory.currentItem = heldItem;
            }
        }
    }

    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW) {
        BlockPos underPos = new BlockPos(x, Scaffold.mc.player.posY - (double)(Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue() != false ? 2 : 1), z);
        Block underBlock = Scaffold.mc.world.getBlockState(underPos).getBlock();
        double xCalc = -999.0;
        double zCalc = -999.0;
        double dist = 0.0;
        double expandDist = this.expand.getValue().floatValue() * 2.0f;
        while (!this.canPlace(underBlock)) {
            xCalc = x;
            zCalc = z;
            if ((dist += 1.0) > expandDist) {
                dist = expandDist;
            }
            xCalc += (forward * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f))) * dist;
            zCalc += (forward * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f))) * dist;
            if (dist == expandDist) break;
            underPos = new BlockPos(xCalc, Scaffold.mc.player.posY - (double)(Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && this.down.getValue() != false ? 2 : 1), zCalc);
            underBlock = Scaffold.mc.world.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc, zCalc};
    }

    public boolean canPlace(Block block) {
        return (block instanceof BlockAir || block instanceof BlockLiquid) && Scaffold.mc.world != null && Scaffold.mc.player != null && this.pos != null && Scaffold.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(this.pos)).isEmpty();
    }

    private int getBlockCountHotbar() {
        int blockCount = 0;
        for (int i = 36; i < 45; ++i) {
            if (!Scaffold.mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack is = Scaffold.mc.player.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (!(is.getItem() instanceof ItemBlock) || this.invalid.contains(((ItemBlock)((Object)item)).getBlock())) continue;
            blockCount += is.getCount();
        }
        return blockCount;
    }

    private BlockData getBlockData2(BlockPos pos) {
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        BlockPos pos2 = pos.add(-1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, 1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, 0, -1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(-2, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(2, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, 2);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, -2);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos10 = pos.add(0, -1, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos10.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos10.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos10.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos10.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos10.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos10.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos10.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos11 = pos10.add(1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos11.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos11.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos11.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos11.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos11.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos11.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos11.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos12 = pos10.add(-1, 0, 0);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos12.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos12.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos12.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos12.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos12.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos12.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos12.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos13 = pos10.add(0, 0, 1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos13.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos13.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos13.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos13.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos13.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos13.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos13.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos14 = pos10.add(0, 0, -1);
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos14.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(0, 1, 0)).getBlock())) {
            return new BlockData(pos14.add(0, 1, 0), EnumFacing.DOWN);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos14.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos14.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos14.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!this.invalid.contains(Scaffold.mc.world.getBlockState(pos14.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos14.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    public void place(BlockPos posI, EnumFacing face) {
        BlockPos pos = posI;
        if (face == EnumFacing.UP) {
            pos = pos.add(0, -1, 0);
        } else if (face == EnumFacing.NORTH) {
            pos = pos.add(0, 0, 1);
        } else if (face == EnumFacing.SOUTH) {
            pos = pos.add(0, 0, -1);
        } else if (face == EnumFacing.EAST) {
            pos = pos.add(-1, 0, 0);
        } else if (face == EnumFacing.WEST) {
            pos = pos.add(1, 0, 0);
        }
        int oldSlot = Scaffold.mc.player.inventory.currentItem;
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Scaffold.mc.player.inventory.getStackInSlot(i);
            if (InventoryUtil.isNull(stack) || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem((Item)stack.getItem()).getDefaultState().isFullBlock()) continue;
            newSlot = i;
            break;
        }
        if (newSlot == -1) {
            return;
        }
        boolean crouched = false;
        if (!Scaffold.mc.player.isSneaking() && BlockUtil.blackList.contains(Scaffold.mc.world.getBlockState(pos).getBlock())) {
            Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            crouched = true;
        }
        if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
            Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
            Scaffold.mc.player.inventory.currentItem = newSlot;
            Scaffold.mc.playerController.updateController();
        }
        if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            EntityPlayerSP player = Scaffold.mc.player;
            player.motionX *= 0.3;
            EntityPlayerSP player2 = Scaffold.mc.player;
            player2.motionZ *= 0.3;
            Scaffold.mc.player.jump();
            Scaffold.mc.player.motionY = -0.28;
            this.timer.reset();
        }
        if (this.rotation.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(Scaffold.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float)pos.getX() + 0.5f, (float)pos.getY() - 0.5f, (float)pos.getZ() + 0.5f));
            Scaffold.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], MathHelper.normalizeAngle((int)((int)angle[1]), (int)360), Scaffold.mc.player.onGround));
        }
        Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
        Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
        Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        Scaffold.mc.player.inventory.currentItem = oldSlot;
        Scaffold.mc.playerController.updateController();
        if (crouched) {
            Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    private static class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

    public static enum Mode {
        Legit,
        Fast;

    }
}
