package me.primooctopus33.octohack.client.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.DamageUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.RotationUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedAura
extends Module {
    private final Setting<Boolean> place = this.register(new Setting<Boolean>("Place", false));
    private final Setting<Integer> placeDelay = this.register(new Setting<Object>("Placedelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.place.getValue()));
    private final Setting<Float> placeRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.place.getValue()));
    private final Setting<Boolean> extraPacket = this.register(new Setting<Object>("InsanePacket", Boolean.valueOf(false), v -> this.place.getValue()));
    private final Setting<Boolean> packet = this.register(new Setting<Object>("Packet", Boolean.valueOf(false), v -> this.place.getValue()));
    private final Setting<Boolean> explode = this.register(new Setting<Boolean>("Break", true));
    private final Setting<BreakLogic> breakMode = this.register(new Setting<Object>("BreakMode", (Object)BreakLogic.ALL, v -> this.explode.getValue()));
    private final Setting<Integer> breakDelay = this.register(new Setting<Object>("Breakdelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.explode.getValue()));
    private final Setting<Float> breakRange = this.register(new Setting<Object>("BreakRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.explode.getValue()));
    private final Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(36.0f), v -> this.explode.getValue()));
    private final Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(12.0f), v -> this.explode.getValue()));
    private final Setting<Boolean> suicide = this.register(new Setting<Object>("Suicide", Boolean.valueOf(false), v -> this.explode.getValue()));
    private final Setting<Boolean> removeTiles = this.register(new Setting<Boolean>("RemoveTiles", false));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    private final Setting<Boolean> dimensionCheck = this.register(new Setting<Boolean>("Dimension Check", true));
    private final Setting<SwitchModes> switchMode = this.register(new Setting<SwitchModes>("Switch Mode", SwitchModes.SILENT));
    private final Setting<Logic> logic = this.register(new Setting<Object>("Logic", (Object)Logic.BREAKPLACE, v -> this.place.getValue() != false && this.explode.getValue() != false));
    private final Setting<Boolean> craft = this.register(new Setting<Boolean>("Craft", false));
    private final Setting<Boolean> placeCraftingTable = this.register(new Setting<Object>("PlaceTable", Boolean.valueOf(false), v -> this.craft.getValue()));
    private final Setting<Boolean> openCraftingTable = this.register(new Setting<Object>("OpenTable", Boolean.valueOf(false), v -> this.craft.getValue()));
    private final Setting<Boolean> craftTable = this.register(new Setting<Object>("CraftTable", Boolean.valueOf(false), v -> this.craft.getValue()));
    private final Setting<Float> tableRange = this.register(new Setting<Object>("TableRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.craft.getValue()));
    private final Setting<Integer> craftDelay = this.register(new Setting<Object>("CraftDelay", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(10), v -> this.craft.getValue()));
    private final Setting<Integer> tableSlot = this.register(new Setting<Object>("TableSlot", Integer.valueOf(8), Integer.valueOf(0), Integer.valueOf(8), v -> this.craft.getValue()));
    private final Setting<Boolean> sslot = this.register(new Setting<Boolean>("S-Slot", false));
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer craftTimer = new Timer();
    private final AtomicDouble yaw = new AtomicDouble(-1.0);
    private final AtomicDouble pitch = new AtomicDouble(-1.0);
    private final AtomicBoolean shouldRotate = new AtomicBoolean(false);
    private EntityPlayer target = null;
    private boolean sendRotationPacket = false;
    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;
    private BlockPos maxPos = null;
    private boolean shouldCraft;
    private int craftStage = 0;
    private int lastHotbarSlot = -1;
    private int bedSlot = -1;
    private BlockPos finalPos;
    private EnumFacing finalFacing;

    public BedAura() {
        super("BedAura", "AutoPlace and Break for beds", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (this.dimensionCheck.getValue().booleanValue() && BedAura.mc.player.dimension == 0) {
            Command.sendMessage(ChatFormatting.WHITE + "<DimensionCheck> You are in the Overworld! Toggling Off!");
            this.disable();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (this.shouldRotate.get() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.yaw = (float)this.yaw.get();
            packet.pitch = (float)this.pitch.get();
            this.shouldRotate.set(false);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            this.doBedAura();
            if (this.shouldCraft && BedAura.mc.currentScreen instanceof GuiCrafting) {
                int woolSlot = InventoryUtil.findInventoryWool(false);
                int woodSlot = InventoryUtil.findInventoryBlock(BlockPlanks.class, true);
                if (woolSlot == -1 || woodSlot == -1) {
                    mc.displayGuiScreen(null);
                    BedAura.mc.currentScreen = null;
                    this.shouldCraft = false;
                    return;
                }
                if (this.craftStage > 1 && !this.one) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 1, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.one = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() && !this.two) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 2, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.two = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 2 && !this.three) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 3, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.three = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 3 && !this.four) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 4, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.four = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 4 && !this.five) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 5, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.five = true;
                } else if (this.craftStage > 1 + this.craftDelay.getValue() * 5 && !this.six) {
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 6, 1, ClickType.PICKUP, BedAura.mc.player);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
                    this.recheckBedSlots(woolSlot, woodSlot);
                    BedAura.mc.playerController.windowClick(((GuiContainer)((Object)BedAura.mc.currentScreen)).inventorySlots.windowId, 0, 0, ClickType.QUICK_MOVE, BedAura.mc.player);
                    this.six = true;
                    this.one = false;
                    this.two = false;
                    this.three = false;
                    this.four = false;
                    this.five = false;
                    this.six = false;
                    this.craftStage = -2;
                    this.shouldCraft = false;
                }
                ++this.craftStage;
            }
        } else if (event.getStage() == 1 && this.finalPos != null) {
            Vec3d hitVec = new Vec3d(this.finalPos.down()).addVector(0.5, 0.5, 0.5).add(new Vec3d(this.finalFacing.getOpposite().getDirectionVec()).scale(0.5));
            BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            switch (this.switchMode.getValue()) {
                case NORMAL: {
                    InventoryUtil.switchToHotbarSlot(this.bedSlot, false);
                    break;
                }
                case SILENT: {
                    InventoryUtil.switchToHotbarSlot(this.bedSlot, true);
                }
            }
            BlockUtil.rightClickBlock(this.finalPos.down(), hitVec, this.bedSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, this.packet.getValue());
            BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.placeTimer.reset();
            this.finalPos = null;
        }
    }

    public void recheckBedSlots(int woolSlot, int woodSlot) {
        int i;
        for (i = 1; i <= 3; ++i) {
            if (BedAura.mc.player.openContainer.getInventory().get(i) != ItemStack.EMPTY) continue;
            BedAura.mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(1, woolSlot, 0, ClickType.PICKUP, BedAura.mc.player);
        }
        for (i = 4; i <= 6; ++i) {
            if (BedAura.mc.player.openContainer.getInventory().get(i) != ItemStack.EMPTY) continue;
            BedAura.mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(1, i, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(1, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
        }
    }

    public void incrementCraftStage() {
        if (this.craftTimer.passedMs(this.craftDelay.getValue().intValue())) {
            ++this.craftStage;
            if (this.craftStage > 9) {
                this.craftStage = 0;
            }
            this.craftTimer.reset();
        }
    }

    private void doBedAura() {
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.mapBeds();
                this.breakBeds();
                this.placeBeds();
                break;
            }
            case PLACEBREAK: {
                this.mapBeds();
                this.placeBeds();
                this.breakBeds();
            }
        }
    }

    private void breakBeds() {
        if (this.explode.getValue().booleanValue() && this.breakTimer.passedMs(this.breakDelay.getValue().intValue())) {
            if (this.breakMode.getValue() == BreakLogic.CALC) {
                if (this.maxPos != null) {
                    RayTraceResult result;
                    Vec3d hitVec = new Vec3d(this.maxPos).addVector(0.5, 0.5, 0.5);
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    this.yaw.set(rotations[0]);
                    if (this.rotate.getValue().booleanValue()) {
                        this.shouldRotate.set(true);
                        this.pitch.set(rotations[1]);
                    }
                    EnumFacing facing = (result = BedAura.mc.world.rayTraceBlocks(new Vec3d(BedAura.mc.player.posX, BedAura.mc.player.posY + (double)BedAura.mc.player.getEyeHeight(), BedAura.mc.player.posZ), new Vec3d((double)this.maxPos.getX() + 0.5, (double)this.maxPos.getY() - 0.5, (double)this.maxPos.getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                    BlockUtil.rightClickBlock(this.maxPos, hitVec, EnumHand.MAIN_HAND, facing, true);
                    this.breakTimer.reset();
                }
            } else {
                for (TileEntity entityBed : BedAura.mc.world.loadedTileEntityList) {
                    RayTraceResult result;
                    if (!(entityBed instanceof TileEntityBed) || BedAura.mc.player.getDistanceSq(entityBed.getPos()) > MathUtil.square(this.breakRange.getValue().floatValue())) continue;
                    Vec3d hitVec = new Vec3d(entityBed.getPos()).addVector(0.5, 0.5, 0.5);
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    this.yaw.set(rotations[0]);
                    if (this.rotate.getValue().booleanValue()) {
                        this.shouldRotate.set(true);
                        this.pitch.set(rotations[1]);
                    }
                    EnumFacing facing = (result = BedAura.mc.world.rayTraceBlocks(new Vec3d(BedAura.mc.player.posX, BedAura.mc.player.posY + (double)BedAura.mc.player.getEyeHeight(), BedAura.mc.player.posZ), new Vec3d((double)entityBed.getPos().getX() + 0.5, (double)entityBed.getPos().getY() - 0.5, (double)entityBed.getPos().getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                    BlockUtil.rightClickBlock(entityBed.getPos(), hitVec, EnumHand.MAIN_HAND, facing, true);
                    this.breakTimer.reset();
                }
            }
        }
    }

    private void mapBeds() {
        this.maxPos = null;
        float maxDamage = 0.5f;
        if (this.removeTiles.getValue().booleanValue()) {
            ArrayList<BedData> removedBlocks = new ArrayList<BedData>();
            for (TileEntity tile : BedAura.mc.world.loadedTileEntityList) {
                if (!(tile instanceof TileEntityBed)) continue;
                TileEntityBed bed = (TileEntityBed)((Object)tile);
                BedData data = new BedData(tile.getPos(), BedAura.mc.world.getBlockState(tile.getPos()), bed, bed.isHeadPiece());
                removedBlocks.add(data);
            }
            for (BedData data : removedBlocks) {
                BedAura.mc.world.setBlockToAir(data.getPos());
            }
            for (BedData data : removedBlocks) {
                float selfDamage;
                BlockPos blockPos;
                if (!data.isHeadPiece()) continue;
                BlockPos pos = data.getPos();
                if (!(BedAura.mc.player.getDistanceSq(blockPos) <= MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedAura.mc.player)) + 1.0 < (double)EntityUtil.getHealth(BedAura.mc.player)) && DamageUtil.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedAura.mc.world.playerEntities) {
                    float damage;
                    if (!(player.getDistanceSq(pos) < MathUtil.square(this.range.getValue().floatValue()) && EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) && ((damage = DamageUtil.calculateDamage(pos, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue()) || damage > EntityUtil.getHealth(player)) && damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
            for (BedData data : removedBlocks) {
                BedAura.mc.world.setBlockState(data.getPos(), data.getState());
            }
        } else {
            for (TileEntity tile : BedAura.mc.world.loadedTileEntityList) {
                float selfDamage;
                BlockPos blockPos;
                TileEntityBed bed;
                if (!(tile instanceof TileEntityBed) || !(bed = (TileEntityBed)((Object)tile)).isHeadPiece()) continue;
                BlockPos pos = bed.getPos();
                if (!(BedAura.mc.player.getDistanceSq(blockPos) <= MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedAura.mc.player)) + 1.0 < (double)EntityUtil.getHealth(BedAura.mc.player)) && DamageUtil.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedAura.mc.world.playerEntities) {
                    float damage;
                    if (!(player.getDistanceSq(pos) < MathUtil.square(this.range.getValue().floatValue()) && EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) && ((damage = DamageUtil.calculateDamage(pos, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue()) || damage > EntityUtil.getHealth(player)) && damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
        }
    }

    private void placeBeds() {
        if (this.place.getValue().booleanValue() && this.placeTimer.passedMs(this.placeDelay.getValue().intValue()) && this.maxPos == null) {
            this.bedSlot = this.findBedSlot();
            if (this.bedSlot == -1) {
                if (BedAura.mc.player.getHeldItemOffhand().getItem() == Items.BED) {
                    this.bedSlot = -2;
                } else {
                    if (this.craft.getValue().booleanValue() && !this.shouldCraft && EntityUtil.getClosestEnemy(this.placeRange.getValue().floatValue()) != null) {
                        this.doBedCraft();
                    }
                    return;
                }
            }
            this.lastHotbarSlot = BedAura.mc.player.inventory.currentItem;
            this.target = EntityUtil.getClosestEnemy(this.placeRange.getValue().floatValue());
            if (this.target != null) {
                BlockPos targetPos = new BlockPos(this.target.getPositionVector());
                this.placeBed(targetPos, true);
                if (this.craft.getValue().booleanValue()) {
                    this.doBedCraft();
                }
            }
        }
    }

    private void placeBed(BlockPos pos, boolean firstCheck) {
        if (BedAura.mc.world.getBlockState(pos).getBlock() == Blocks.BED) {
            return;
        }
        float damage = DamageUtil.calculateDamage(pos, (Entity)BedAura.mc.player);
        if ((double)damage > (double)EntityUtil.getHealth(BedAura.mc.player) + 0.5) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        if (!BedAura.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        HashMap<BlockPos, EnumFacing> facings = new HashMap<BlockPos, EnumFacing>();
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos blockPos;
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) continue;
            BlockPos position = pos.offset(facing);
            if (!(BedAura.mc.player.getDistanceSq(blockPos) <= MathUtil.square(this.placeRange.getValue().floatValue())) || !BedAura.mc.world.getBlockState(position).getMaterial().isReplaceable() || BedAura.mc.world.getBlockState(position.down()).getMaterial().isReplaceable()) continue;
            positions.add(position);
            facings.put(position, facing.getOpposite());
        }
        if (positions.isEmpty()) {
            if (firstCheck && this.oneDot15.getValue().booleanValue()) {
                this.placeBed(pos.up(), false);
            }
            return;
        }
        positions.sort(Comparator.comparingDouble(pos2 -> BedAura.mc.player.getDistanceSq((BlockPos)pos2)));
        this.finalPos = (BlockPos)positions.get(0);
        this.finalFacing = (EnumFacing)facings.get(this.finalPos);
        float[] rotation = RotationUtil.simpleFacing(this.finalFacing);
        if (!this.sendRotationPacket && this.extraPacket.getValue().booleanValue()) {
            RotationUtil.faceYawAndPitch(rotation[0], rotation[1]);
            this.sendRotationPacket = true;
        }
        this.yaw.set(rotation[0]);
        this.pitch.set(rotation[1]);
        this.shouldRotate.set(true);
        OctoHack.rotationManager.setPlayerRotations(rotation[0], rotation[1]);
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    public void doBedCraft() {
        BlockPos target;
        List targets;
        int woolSlot = InventoryUtil.findInventoryWool(false);
        int woodSlot = InventoryUtil.findInventoryBlock(BlockPlanks.class, true);
        if (woolSlot == -1 || woodSlot == -1) {
            if (BedAura.mc.currentScreen instanceof GuiCrafting) {
                mc.displayGuiScreen(null);
                BedAura.mc.currentScreen = null;
            }
            return;
        }
        if (this.placeCraftingTable.getValue().booleanValue() && BlockUtil.getBlockSphere(this.tableRange.getValue().floatValue() - 1.0f, BlockWorkbench.class).size() == 0 && !(targets = BlockUtil.getSphere(EntityUtil.getPlayerPos(BedAura.mc.player), this.tableRange.getValue().floatValue(), this.tableRange.getValue().intValue(), false, true, 0).stream().filter(pos -> BlockUtil.isPositionPlaceable(pos, false) == 3).sorted(Comparator.comparingInt(pos -> -this.safety((BlockPos)pos))).collect(Collectors.toList())).isEmpty()) {
            target = (BlockPos)targets.get(0);
            int tableSlot = InventoryUtil.findHotbarBlock(BlockWorkbench.class);
            if (tableSlot != -1) {
                BedAura.mc.player.inventory.currentItem = tableSlot;
                BlockUtil.placeBlock(target, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false, false);
            } else {
                if (this.craftTable.getValue().booleanValue()) {
                    this.craftTable();
                }
                if ((tableSlot = InventoryUtil.findHotbarBlock(BlockWorkbench.class)) != -1) {
                    BedAura.mc.player.inventory.currentItem = tableSlot;
                    BlockUtil.placeBlock(target, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false, false);
                }
            }
        }
        if (this.openCraftingTable.getValue().booleanValue()) {
            List<BlockPos> tables = BlockUtil.getBlockSphere(this.tableRange.getValue().floatValue(), BlockWorkbench.class);
            tables.sort(Comparator.comparingDouble(pos -> BedAura.mc.player.getDistanceSq((BlockPos)pos)));
            if (!tables.isEmpty() && !(BedAura.mc.currentScreen instanceof GuiCrafting)) {
                RayTraceResult result;
                target = tables.get(0);
                BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                if (BedAura.mc.player.getDistanceSq(target) > MathUtil.square(this.breakRange.getValue().floatValue())) {
                    return;
                }
                Vec3d hitVec = new Vec3d(target);
                float[] rotations = RotationUtil.getLegitRotations(hitVec);
                this.yaw.set(rotations[0]);
                if (this.rotate.getValue().booleanValue()) {
                    this.shouldRotate.set(true);
                    this.pitch.set(rotations[1]);
                }
                EnumFacing facing = (result = BedAura.mc.world.rayTraceBlocks(new Vec3d(BedAura.mc.player.posX, BedAura.mc.player.posY + (double)BedAura.mc.player.getEyeHeight(), BedAura.mc.player.posZ), new Vec3d((double)target.getX() + 0.5, (double)target.getY() - 0.5, (double)target.getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                BlockUtil.rightClickBlock(target, hitVec, EnumHand.MAIN_HAND, facing, true);
                this.breakTimer.reset();
                if (BedAura.mc.player.isSneaking()) {
                    BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }
            this.shouldCraft = BedAura.mc.currentScreen instanceof GuiCrafting;
            this.craftStage = 0;
            this.craftTimer.reset();
        }
    }

    public void craftTable() {
        int woodSlot = InventoryUtil.findInventoryBlock(BlockPlanks.class, true);
        if (woodSlot != -1) {
            BedAura.mc.playerController.windowClick(0, woodSlot, 0, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(0, 1, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(0, 2, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(0, 3, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(0, 4, 1, ClickType.PICKUP, BedAura.mc.player);
            BedAura.mc.playerController.windowClick(0, 0, 0, ClickType.QUICK_MOVE, BedAura.mc.player);
            int table = InventoryUtil.findInventoryBlock(BlockWorkbench.class, true);
            if (table != -1) {
                BedAura.mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, BedAura.mc.player);
                BedAura.mc.playerController.windowClick(0, this.tableSlot.getValue(), 0, ClickType.PICKUP, BedAura.mc.player);
                BedAura.mc.playerController.windowClick(0, table, 0, ClickType.PICKUP, BedAura.mc.player);
            }
        }
    }

    @Override
    public void onToggle() {
        this.lastHotbarSlot = -1;
        this.bedSlot = -1;
        this.sendRotationPacket = false;
        this.target = null;
        this.yaw.set(-1.0);
        this.pitch.set(-1.0);
        this.shouldRotate.set(false);
        this.shouldCraft = false;
    }

    private int findBedSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BedAura.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || stack.getItem() != Items.BED) continue;
            return i;
        }
        return -1;
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (BedAura.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) continue;
            ++safety;
        }
        return safety;
    }

    public static class BedData {
        private final BlockPos pos;
        private final IBlockState state;
        private final boolean isHeadPiece;
        private final TileEntityBed entity;

        public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
            this.isHeadPiece = isHeadPiece;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public IBlockState getState() {
            return this.state;
        }

        public boolean isHeadPiece() {
            return this.isHeadPiece;
        }

        public TileEntityBed getEntity() {
            return this.entity;
        }
    }

    public static enum Logic {
        BREAKPLACE,
        PLACEBREAK;

    }

    public static enum BreakLogic {
        ALL,
        CALC;

    }

    public static enum SwitchModes {
        SILENT,
        NORMAL;

    }
}
