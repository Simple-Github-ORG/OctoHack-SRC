package me.primooctopus33.octohack.client.modules.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ShulkerCrystal
extends Module {
    public final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    private int oldSlot;
    private int shulkerSlot;
    private int crystalSlot;
    private int waitTicks;
    private boolean doShulker;
    private boolean doCrystal;
    private boolean openShulker;
    private boolean detonate;
    private boolean finishedDetonate;
    private BlockPos shulkerSpot;
    private BlockPos crystalSpot;
    private direction spoofDirection;
    private EntityPlayer target;
    private Setting<Boolean> debug = this.register(new Setting<Boolean>("Debug", false));
    private Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotation Lock", false));
    private Setting<Boolean> antiWeakness = this.register(new Setting<Boolean>("Anti Weakness", false));
    private Setting<Integer> detonateDelay = this.register(new Setting<Integer>("Detonate Delay", 4, 1, 10));
    private Setting<Integer> endDelay = this.register(new Setting<Integer>("Await Delay", 4, 1, 10));
    private Setting<Integer> restartDelay = this.register(new Setting<Integer>("Attempt Delay", 4, 1, 10));

    public ShulkerCrystal() {
        super("ShulkerCrystal", "Uses shulkers to push crystals into enemies", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.oldSlot = ShulkerCrystal.mc.player.inventory.currentItem;
        this.spoofDirection = direction.NORTH;
        this.target = null;
        this.doShulker = false;
        this.doCrystal = false;
        this.openShulker = false;
        this.detonate = false;
        this.finishedDetonate = false;
        this.crystalSlot = -1;
        this.shulkerSlot = -1;
        this.waitTicks = 0;
    }

    @Override
    public void onDisable() {
        this.oldSlot = ShulkerCrystal.mc.player.inventory.currentItem;
        ShulkerCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
    }

    private EntityPlayer getTarget() {
        EntityPlayer temp = null;
        for (EntityPlayer player : ShulkerCrystal.mc.world.playerEntities) {
            if (player == null || player == ShulkerCrystal.mc.player || !(player.getHealth() > 0.0f) || !(ShulkerCrystal.mc.player.getDistance(player) < 5.0f) || OctoHack.friendManager.isFriend(player.getName())) continue;
            temp = player;
        }
        if (temp != null && this.debug.getValue().booleanValue()) {
            Command.sendMessage("Target Set: " + temp.getName());
        }
        return temp;
    }

    @Override
    public void onUpdate() {
        if (this.doShulker) {
            this.shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.shulkerSlot));
            this.placeBlock(this.shulkerSpot);
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            this.doShulker = false;
            return;
        }
        if (this.doCrystal) {
            this.crystalSlot = InventoryUtil.find(Items.END_CRYSTAL);
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.crystalSlot));
            this.placeCrystal(this.crystalSpot);
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            this.doCrystal = false;
            this.openShulker = true;
            return;
        }
        if (this.openShulker) {
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.shulkerSpot, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            if (ShulkerCrystal.mc.currentScreen instanceof GuiShulkerBox) {
                ShulkerCrystal.mc.playerController.updateController();
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Closing Shulker");
                }
                ShulkerCrystal.mc.player.closeScreenAndDropStack();
                this.openShulker = false;
                this.detonate = true;
                this.waitTicks = 0;
            }
            return;
        }
        if (this.detonate) {
            if (this.waitTicks++ > this.detonateDelay.getValue()) {
                if (this.waitTicks - this.detonateDelay.getValue() > this.restartDelay.getValue()) {
                    if (this.debug.getValue().booleanValue()) {
                        Command.sendMessage("Re-Attempting");
                    }
                    this.detonate = false;
                    this.doCrystal = true;
                }
                for (Entity e : ShulkerCrystal.mc.world.loadedEntityList) {
                    if (!(e instanceof EntityEnderCrystal) || e == null || e.isDead || !(ShulkerCrystal.mc.player.getDistance(e) < 5.0f)) continue;
                    ShulkerCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(e));
                    ShulkerCrystal.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    this.detonate = false;
                    this.finishedDetonate = true;
                    this.waitTicks = 0;
                }
            }
            return;
        }
        if (this.finishedDetonate) {
            if (this.waitTicks++ > this.endDelay.getValue()) {
                this.finishedDetonate = false;
            }
            return;
        }
        this.target = this.getTarget();
        this.oldSlot = ShulkerCrystal.mc.player.inventory.currentItem;
        if (this.target != null) {
            Vec3d offset1 = this.target.getPositionVector().addVector(1.0, 0.0, 0.0);
            Vec3d offset2 = this.target.getPositionVector().addVector(2.0, 0.0, 0.0);
            Vec3d offset3 = this.target.getPositionVector().addVector(3.0, 0.0, 0.0);
            Vec3d offset4 = this.target.getPositionVector().addVector(1.0, 1.0, 0.0);
            Vec3d offset5 = this.target.getPositionVector().addVector(2.0, 1.0, 0.0);
            Vec3d offset6 = this.target.getPositionVector().addVector(3.0, 1.0, 0.0);
            if (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Target is vulnerable!");
                }
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Method 1");
                }
                this.spoofDirection = direction.EAST;
                this.shulkerSlot = -1;
                this.crystalSlot = -1;
                if (this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                    if (this.debug.getValue().booleanValue()) {
                        Command.sendMessage("Shulker already in place.");
                    }
                    this.shulkerSlot = 1337;
                } else {
                    for (Block b : this.shulkerList) {
                        if (this.findHotbarBlock(b) == -1) continue;
                        this.shulkerSlot = this.findHotbarBlock(b);
                        break;
                    }
                }
                this.crystalSlot = InventoryUtil.find(Items.END_CRYSTAL);
                if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                    this.shulkerSpot = new BlockPos(offset5);
                    this.crystalSpot = new BlockPos(offset1);
                    if (!this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        this.doShulker = true;
                    }
                    this.doCrystal = true;
                    return;
                }
            }
            offset1 = this.target.getPositionVector().addVector(-1.0, 0.0, 0.0);
            offset2 = this.target.getPositionVector().addVector(-2.0, 0.0, 0.0);
            offset3 = this.target.getPositionVector().addVector(-3.0, 0.0, 0.0);
            offset4 = this.target.getPositionVector().addVector(-1.0, 1.0, 0.0);
            offset5 = this.target.getPositionVector().addVector(-2.0, 1.0, 0.0);
            offset6 = this.target.getPositionVector().addVector(-3.0, 1.0, 0.0);
            if (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Target is vulnerable!");
                }
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Method 2");
                }
                this.spoofDirection = direction.WEST;
                this.shulkerSlot = -1;
                this.crystalSlot = -1;
                if (this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                    if (this.debug.getValue().booleanValue()) {
                        Command.sendMessage("Shulker already in place.");
                    }
                    this.shulkerSlot = 1337;
                } else {
                    for (Block b : this.shulkerList) {
                        if (this.findHotbarBlock(b) == -1) continue;
                        this.shulkerSlot = this.findHotbarBlock(b);
                        break;
                    }
                }
                this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                    this.shulkerSpot = new BlockPos(offset5);
                    this.crystalSpot = new BlockPos(offset1);
                    if (!this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        this.doShulker = true;
                    }
                    this.doCrystal = true;
                    return;
                }
            }
            offset1 = this.target.getPositionVector().addVector(0.0, 0.0, 1.0);
            offset2 = this.target.getPositionVector().addVector(0.0, 0.0, 2.0);
            offset3 = this.target.getPositionVector().addVector(0.0, 0.0, 3.0);
            offset4 = this.target.getPositionVector().addVector(0.0, 1.0, 1.0);
            offset5 = this.target.getPositionVector().addVector(0.0, 1.0, 2.0);
            offset6 = this.target.getPositionVector().addVector(0.0, 1.0, 3.0);
            if (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Target is vulnerable!");
                }
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Method 3");
                }
                this.spoofDirection = direction.SOUTH;
                this.shulkerSlot = -1;
                this.crystalSlot = -1;
                if (this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                    if (this.debug.getValue().booleanValue()) {
                        Command.sendMessage("Shulker already in place.");
                    }
                    this.shulkerSlot = 1337;
                } else {
                    for (Block b : this.shulkerList) {
                        if (this.findHotbarBlock(b) == -1) continue;
                        this.shulkerSlot = this.findHotbarBlock(b);
                        break;
                    }
                }
                this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                    this.shulkerSpot = new BlockPos(offset5);
                    this.crystalSpot = new BlockPos(offset1);
                    if (!this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        this.doShulker = true;
                    }
                    this.doCrystal = true;
                    return;
                }
            }
            offset1 = this.target.getPositionVector().addVector(0.0, 0.0, -1.0);
            offset2 = this.target.getPositionVector().addVector(0.0, 0.0, -2.0);
            offset3 = this.target.getPositionVector().addVector(0.0, 0.0, -3.0);
            offset4 = this.target.getPositionVector().addVector(0.0, 1.0, -1.0);
            offset5 = this.target.getPositionVector().addVector(0.0, 1.0, -2.0);
            offset6 = this.target.getPositionVector().addVector(0.0, 1.0, -3.0);
            if (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Target is vulnerable!");
                }
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("Method 4");
                }
                this.spoofDirection = direction.NORTH;
                this.shulkerSlot = -1;
                this.crystalSlot = -1;
                if (this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                    if (this.debug.getValue().booleanValue()) {
                        Command.sendMessage("Shulker already in place.");
                    }
                    this.shulkerSlot = 1337;
                } else {
                    for (Block b : this.shulkerList) {
                        if (this.findHotbarBlock(b) == -1) continue;
                        this.shulkerSlot = this.findHotbarBlock(b);
                        break;
                    }
                }
                this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                    this.shulkerSpot = new BlockPos(offset5);
                    this.crystalSpot = new BlockPos(offset1);
                    if (!this.shulkerList.contains(ShulkerCrystal.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        this.doShulker = true;
                    }
                    this.doCrystal = true;
                    return;
                }
            }
        }
    }

    public int findHotbarItem(Item itemIn) {
        for (int i = 0; i < 9; ++i) {
            Item item;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof Item) || (item = stack.getItem()) != itemIn) continue;
            return i;
        }
        return -1;
    }

    public int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock)((Object)stack.getItem())).getBlock()) != blockIn) continue;
            return i;
        }
        return -1;
    }

    private void placeCrystal(BlockPos pos) {
        if (this.debug.getValue().booleanValue()) {
            Command.sendMessage("Debug " + pos);
        }
        ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
    }

    private void placeBlock(BlockPos pos) {
        if (this.spoofDirection == direction.NORTH) {
            if (this.rotate.getValue().booleanValue()) {
                ShulkerCrystal.mc.player.rotationYaw = 180.0f;
            }
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180.0f, 0.0f, ShulkerCrystal.mc.player.onGround));
        } else if (this.spoofDirection == direction.SOUTH) {
            if (this.rotate.getValue().booleanValue()) {
                ShulkerCrystal.mc.player.rotationYaw = 0.0f;
            }
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0.0f, 0.0f, ShulkerCrystal.mc.player.onGround));
        } else if (this.spoofDirection == direction.WEST) {
            if (this.rotate.getValue().booleanValue()) {
                ShulkerCrystal.mc.player.rotationYaw = 90.0f;
            }
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90.0f, 0.0f, ShulkerCrystal.mc.player.onGround));
        } else if (this.spoofDirection == direction.EAST) {
            if (this.rotate.getValue().booleanValue()) {
                ShulkerCrystal.mc.player.rotationYaw = -90.0f;
            }
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90.0f, 0.0f, ShulkerCrystal.mc.player.onGround));
        }
        boolean isSneaking = this.placeBlock(pos, EnumHand.MAIN_HAND, false, true, ShulkerCrystal.mc.player.isSneaking());
        if (isSneaking) {
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketEntityAction(ShulkerCrystal.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = this.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = ShulkerCrystal.mc.world.getBlockState(neighbour).getBlock();
        if (!ShulkerCrystal.mc.player.isSneaking() && (this.blackList.contains(neighbourBlock) || this.shulkerList.contains(neighbourBlock))) {
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketEntityAction(ShulkerCrystal.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            ShulkerCrystal.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        this.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        ShulkerCrystal.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        ShulkerCrystal.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = this.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        ArrayList<EnumFacing> directions = new ArrayList<EnumFacing>();
        directions.add(EnumFacing.NORTH);
        directions.add(EnumFacing.SOUTH);
        directions.add(EnumFacing.EAST);
        directions.add(EnumFacing.WEST);
        for (EnumFacing side : directions) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!ShulkerCrystal.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(ShulkerCrystal.mc.world.getBlockState(neighbour), false) || (blockState = ShulkerCrystal.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction2, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            ShulkerCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction2, hand, f, f1, f2));
        } else {
            ShulkerCrystal.mc.playerController.processRightClickBlock(ShulkerCrystal.mc.player, ShulkerCrystal.mc.world, pos, direction2, vec, hand);
        }
        ShulkerCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
        ShulkerCrystal.mc.rightClickDelayTimer = 4;
    }

    private static enum direction {
        NORTH,
        SOUTH,
        EAST,
        WEST;

    }
}
